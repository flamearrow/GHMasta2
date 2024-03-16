package band.mlgb.ghmasta2.domain

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import band.mlgb.ghmasta2.database.KeywordReposPageDao
import band.mlgb.ghmasta2.database.RepoDao
import band.mlgb.ghmasta2.network.GHSearchApi
import band.mlgb.ghmasta2.paging.KeywordReposMediator
import band.mlgb.ghmasta2.paging.UserReposPagingSource
import javax.inject.Inject

class SearchRepoWithKeywordRemoteMediatorUsecase @Inject constructor(
    val repoDao: RepoDao,
    val api: GHSearchApi,
    val keywordReposPageDao: KeywordReposPageDao
) {

    @OptIn(ExperimentalPagingApi::class)
    operator fun invoke(
        searchQuery: String // User's login
    ) = Pager(
        config = PagingConfig(UserReposPagingSource.REPO_PER_PAGE),
        remoteMediator = KeywordReposMediator(
            repoKeyword = searchQuery,
            keywordReposPageDao = keywordReposPageDao,
            api = api,
            repoDao = repoDao
        )
    ) {
        repoDao.reposWithKeyword("%${searchQuery}%") // partial match
    }.flow
}