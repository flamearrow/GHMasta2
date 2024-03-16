package band.mlgb.ghmasta2.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import band.mlgb.ghmasta2.database.RepoDao
import band.mlgb.ghmasta2.model.RepoEntity
import band.mlgb.ghmasta2.network.GHSearchApi


/**
 * Creating a PagingSource manually - define when to append(getRefreshKey) and how to append(load)
 *
 * Note: this one manually insert into DB by calling the suspend repoDao.upsertRepos, if anything
 *  updates repoDao, the collectAsLazyPagingItems exposed by the Pager of this source
 *  won't get that update.
 */
class UserReposPagingSource(
    val userId: String,
    val repoDao: RepoDao,
    val api: GHSearchApi
) : PagingSource<Int, RepoEntity>() {
    override fun getRefreshKey(state: PagingState<Int, RepoEntity>): Int {
        return ((state.anchorPosition ?: 0) - state.config.initialLoadSize / 2).coerceAtLeast(0)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RepoEntity> {
        return try {
            val page = params.key ?: 1

            val repos = api.searchUserRepos(userId, page = page, perPage = REPO_PER_PAGE)
            repoDao.upsertRepos(repos)
            LoadResult.Page(
                data = repoDao.reposByIds(repos.map { repo -> repo.id }),
                prevKey = if (page == 1) null else page.minus(1),
                // If we want 20 per page and only returns 19, then it means this is the last page,
                // and there's no nextPage
                nextKey = if (repos.size < REPO_PER_PAGE) null else page.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        const val REPO_PER_PAGE = 5
    }
}