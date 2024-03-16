package band.mlgb.ghmasta2.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import band.mlgb.ghmasta2.database.KeywordReposPageDao
import band.mlgb.ghmasta2.database.RepoDao
import band.mlgb.ghmasta2.model.RepoEntity
import band.mlgb.ghmasta2.network.GHSearchApi
import retrofit2.HttpException

/**
 * This can be merged with UserReposMediator, but who cares
 *
 * This
 */
@OptIn(ExperimentalPagingApi::class)
class KeywordReposMediator(
    val repoKeyword: String,
    val keywordReposPageDao: KeywordReposPageDao,
    val api: GHSearchApi,
    val repoDao: RepoDao
) : RemoteMediator<Int, RepoEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RepoEntity>
    ): MediatorResult {
        val pageToLoad = when (loadType) {
            LoadType.REFRESH -> {
                1
            }

            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            LoadType.APPEND -> {
                state.lastItemOrNull()?.id?.let { lastAccessedRepoId ->
                    keywordReposPageDao.nextPageForRepo(lastAccessedRepoId) ?: run {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                } ?: 1
            }
        }

        try {
            val repos = api.searchRepoWithKeyword(
                query = repoKeyword,
                page = pageToLoad,
                perPage = REPO_PER_PAGE
            ).items
            val hasMore = repos.size == REPO_PER_PAGE
            if (hasMore) {
                keywordReposPageDao.updateKeys(repos, pageToLoad + 1)
            }
            repoDao.upsertRepos(repos)
            return MediatorResult.Success(endOfPaginationReached = !hasMore)
        } catch (e: Exception) {
            return if (e is HttpException && e.code() == 404) { // found nothing
                MediatorResult.Success(endOfPaginationReached = true)
            } else if (e is HttpException && e.code() == 422) { // query is ""
                MediatorResult.Success(endOfPaginationReached = true)
            } else {
                MediatorResult.Error(e)
            }
        }


    }

    companion object {
        const val REPO_PER_PAGE = 5
    }

}