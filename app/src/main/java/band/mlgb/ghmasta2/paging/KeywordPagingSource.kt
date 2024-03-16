package band.mlgb.ghmasta2.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import band.mlgb.ghmasta2.database.RepoDao
import band.mlgb.ghmasta2.model.Repo
import band.mlgb.ghmasta2.model.RepoEntity
import band.mlgb.ghmasta2.network.GHSearchApi
import retrofit2.HttpException

/**
 * In memory keyword search, get [Repo] from server, don't insert into DB
 *  Still need repoDao to check if the repo has been starred or not
 */
class KeywordPagingSource(
    val keyword: String,
    val repoDao: RepoDao,
    val api: GHSearchApi
) : PagingSource<Int, RepoEntity>() {
    override fun getRefreshKey(state: PagingState<Int, RepoEntity>): Int {
        return ((state.anchorPosition ?: 0) - state.config.initialLoadSize / 2).coerceAtLeast(0)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RepoEntity> {
        try {
            val page = params.key ?: 1
            api.searchRepoWithKeyword(keyword, page = page, perPage = REPO_PER_PAGE)
                .let { searchRepoResponse ->
                    return LoadResult.Page(
                        data = searchRepoResponse.items.map {
                            if (repoDao.isStarred(it.id)) it.asStarredEntity() else it.asUnstarredEntity()
                        },
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (searchRepoResponse.items.size < REPO_PER_PAGE) null else page + 1
                    )
                }
        } catch (e: Exception) {
            return if (e is HttpException && e.code() == 404) { // found nothing
                LoadResult.Page(
                    data = listOf(),
                    prevKey = null,
                    nextKey = null
                )
            } else if (e is HttpException && e.code() == 422) { // query is ""
                LoadResult.Page(
                    data = listOf(),
                    prevKey = null,
                    nextKey = null
                )
            } else if (e is HttpException && e.code() == 403) { // rate limited
                LoadResult.Page(
                    data = listOf(),
                    prevKey = null,
                    nextKey = null
                )
            } else {
                LoadResult.Error(e)
            }
        }
    }

    companion object {
        const val REPO_PER_PAGE = 5
    }

}