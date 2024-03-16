package band.mlgb.ghmasta2.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import band.mlgb.ghmasta2.database.RepoDao
import band.mlgb.ghmasta2.network.GHSearchApi
import band.mlgb.ghmasta2.paging.KeywordPagingSource
import javax.inject.Inject

/**
 * In memory keyword search - don't save all the results in db as there're too much
 */
class InMemoryKeywordPagingSearchUseCase @Inject constructor(
    val api: GHSearchApi,
    val repoDao: RepoDao
) {
    operator fun invoke(keyword: String) =
        Pager(
            config = PagingConfig(pageSize = KeywordPagingSource.REPO_PER_PAGE)
        ) {
            KeywordPagingSource(
                keyword = keyword,
                repoDao = repoDao,
                api = api
            )
        }.flow
}