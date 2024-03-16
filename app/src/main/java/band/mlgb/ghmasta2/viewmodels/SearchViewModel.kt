package band.mlgb.ghmasta2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import band.mlgb.ghmasta2.data.SearchQueryRepository
import band.mlgb.ghmasta2.database.RepoDao
import band.mlgb.ghmasta2.database.UserDao
import band.mlgb.ghmasta2.domain.InMemoryKeywordPagingSearchUseCase
import band.mlgb.ghmasta2.domain.UserSearchCase
import band.mlgb.ghmasta2.model.RepoEntity
import band.mlgb.ghmasta2.network.Resource
import band.mlgb.ghmasta2.ui.RecentSearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    val repoDao: RepoDao,
    val userDao: UserDao,
    val userSearchCase: UserSearchCase,
    val inMemoryKeywordPagingSearchUseCase: InMemoryKeywordPagingSearchUseCase,
    val recentSearchQueryRepository: SearchQueryRepository
) : ViewModel() {

    val currentQuery = MutableStateFlow("")

    // Search with a manually created PagingSource, this one doesn't get flow from DB and cannot
    // represent correct UI state if some other source updated DB, e.g clicked star
//    val searchUserRepoPagingResults = currentQuery.flatMapLatest { latestQuery ->
//        Pager(
//            config = PagingConfig(UserReposPagingSource.REPO_PER_PAGE),
//            pagingSourceFactory = {
//                UserReposPagingSource(
//                    userId = latestQuery,
//                    repoDao = repoDao,
//                    api = ghSearchApi
//                )
//            }
//        ).flow
//    }

    // Search with a RemoteMediator backed PagingSource, source is from DB flow, will represent
    // correct UI update whenever the DB is updated.
    // Note this one returns searchReposLazyPagingItems as a state in compose, so can't use stateIn
    //  here, need to directly handle searchReposLazyPagingItems as a state in compose.
//    val searchUserRepoMediatorPagingFlow =
//        currentQuery.flatMapLatest { newQuery ->
//            searchUserRepoRemoteMediatorUseCase(newQuery)
//        }

    // Pager - Search keyword with RemoteMediator backed back db of (repo, nextKey) - not efficient as we
    // need to save all search result in DB
//    val searchKeywordReposMediatorPagingFlow =
//        currentQuery.flatMapLatest { newQuery ->
//            searchRepoWithKeywordRemoteMediatorUseCase(newQuery)
//        }

    // Pager - Search keywrod with inmemory PagingSource.
    //   Search, get Repos, check if the Repo is starred from DB, then return as RepoEntity inmemory
    //   Note the repos returned are NOT insert into DB, making this efficient
    //   Need to manipulate the starred/unstarred state in UX as this is not directly backed from DB.

    val searchKeywordInMemoryPagingFlow =
        currentQuery.debounce(SERACH_INTERVAL).flatMapLatest { newQuery ->
            inMemoryKeywordPagingSearchUseCase(newQuery)
        }


    val userSearchResultFlow =
        currentQuery.debounce(SERACH_INTERVAL).flatMapLatest { newQuery ->
            userSearchCase(newQuery)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Resource.Loading()
        )

    val recentSearchQueryFlow =
        recentSearchQueryRepository.recentSearchQueries.mapLatest { queries ->
            RecentSearchState.Result(
                entries = queries
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = RecentSearchState.Loading
        )

    fun onQueryChanged(newQuery: String) {
        currentQuery.update { newQuery }
    }

    fun addNewRecentEntry(newQuery: String) {
        viewModelScope.launch {
            recentSearchQueryRepository.addNewSearch(newQuery)
        }
    }

    fun clearRecentQueries() {
        viewModelScope.launch {
            recentSearchQueryRepository.clearRecentQueries()
        }
    }

    fun starRepo(repoId: Long) {
        viewModelScope.launch {

            repoDao.star(repoId)
        }
    }

    fun upsertRepo(repo: RepoEntity) {
        viewModelScope.launch {
            repoDao.upsert(repo)
        }
    }


    fun unStarRepo(repoId: Long) {
        viewModelScope.launch {
            repoDao.unstar(repoId)
        }
    }

    fun likeUser(userId: Long) {
        viewModelScope.launch {
            userDao.like(userId)
        }
    }

    fun unlikeUser(userId: Long) {
        viewModelScope.launch {
            userDao.unlike(userId)
        }
    }

    companion object {
        // Min interval to wait until trigger the next search, used to throttle API rate to prevent rate limit
        const val SERACH_INTERVAL: Long = 500
    }
}