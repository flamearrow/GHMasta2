package band.mlgb.ghmasta2.domain

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import band.mlgb.ghmasta2.database.RepoDao
import band.mlgb.ghmasta2.database.UserReposPageDao
import band.mlgb.ghmasta2.network.GHSearchApi
import band.mlgb.ghmasta2.paging.UserReposMediator
import band.mlgb.ghmasta2.paging.UserReposPagingSource
import javax.inject.Inject

class SearchUserRepoRemoteMediatorUsecase @Inject constructor(
    val repoDao: RepoDao,
    val api: GHSearchApi,
    val userReposPageDao: UserReposPageDao
) {

    @OptIn(ExperimentalPagingApi::class)
    operator fun invoke(
        searchQuery: String // User's login
    ) = Pager(
        config = PagingConfig(UserReposPagingSource.REPO_PER_PAGE),
        remoteMediator = UserReposMediator(
            userLogin = searchQuery,
            userReposPageDao = userReposPageDao,
            api = api,
            repoDao = repoDao
        )
    ) {
        repoDao.repoOwnedByUserNamePagingSource(searchQuery)
    }.flow
}