package band.mlgb.ghmasta2.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import band.mlgb.ghmasta2.database.RepoDao
import band.mlgb.ghmasta2.database.UserReposPageDao
import band.mlgb.ghmasta2.model.RepoEntity
import band.mlgb.ghmasta2.network.GHSearchApi
import band.mlgb.ghmasta2.paging.UserReposPagingSource.Companion.REPO_PER_PAGE
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class UserReposMediator(
    val userLogin: String,
    val userReposPageDao: UserReposPageDao,
    val api: GHSearchApi,
    val repoDao: RepoDao,
) : RemoteMediator<Int, RepoEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RepoEntity>
    ): MediatorResult {
        val pageToLoad = when (loadType) {
            LoadType.REFRESH -> {
                1
            } // Load from first
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            } // Don't allow prepend
            LoadType.APPEND -> {
                state.lastItemOrNull()?.id?.let { lastAccessedRepoId ->
                    userReposPageDao.nextPageForRepo(lastAccessedRepoId) ?: run {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                } ?: 1
            }
        }



        try {
            val repos = api.searchUserRepos(
                userLogin,
                page = pageToLoad,
                perPage = REPO_PER_PAGE
            )

            val hasMore = repos.size == REPO_PER_PAGE

            if (hasMore) {
                userReposPageDao.updateKeys(repos, pageToLoad + 1)
            }


            repoDao.upsertRepos(repos)
            return MediatorResult.Success(endOfPaginationReached = !hasMore)
        } catch (e: Exception) {
            // 404 would cause this
            return if (e is HttpException && e.code() == 404) {
                MediatorResult.Success(endOfPaginationReached = true)
            } else {
                MediatorResult.Error(e)
            }
        }
    }
}