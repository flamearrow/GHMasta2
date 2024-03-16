package band.mlgb.ghmasta2.domain

import band.mlgb.ghmasta2.database.RepoDao
import band.mlgb.ghmasta2.model.Repo
import band.mlgb.ghmasta2.model.RepoEntity
import band.mlgb.ghmasta2.network.GHSearchApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import javax.inject.Inject


/**
 * In memory search , save query in memory, search upon changing.
 *
 * This can be overengineered to save the query to db/datastore, and let result observer the query
 * and trigger search accordingly.
 *
 */
@OptIn(ExperimentalCoroutinesApi::class)
class InMemorySearchUseCase @Inject constructor(
    private val ghSearchApi: GHSearchApi,
    private val repoDao: RepoDao
) {

    // For Inmeory, no paging/db
//    val currentQuery: StateFlow<String> =
//        inMemorySearchRepoUseCase.query.stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.Lazily,
//            initialValue = ""
//        )
//    val searchState: StateFlow<SearchState> =
//        inMemorySearchRepoUseCase.searchResultRepoEntities.map { repos ->
//            if (repos == null) {
//                SearchState.Searching
//            } else {
//                SearchState.Result(
//                    repos = repos
//                )
//            }
//        }.stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.Lazily,
//            initialValue = SearchState.Idle
//        )

    val query: MutableStateFlow<String> = MutableStateFlow("")

    // Search base on query, save the returned repos to database and emit all searched Ids
    val searchResultRepos: Flow<List<Repo>?> = query.flatMapLatest {
        flow {
            emit(null) // this triggers the searching state in SearchViewModel, we can also directly emit the searchign state here
            emit(
                try {
                    val repos = ghSearchApi.searchUserRepos(it)
                    repoDao.upsertRepos(repos)
                    repos
                } catch (exception: Exception) {
                    listOf()
                }
            )
        }
    }

    // Note: this flow will gets updated if repoDao is updated, because repoDao.reposByIds returns a
    //  flow that will trigger all the subsequent flows.
    val searchResultRepoEntities: Flow<List<RepoEntity>?> =
        searchResultRepos.flatMapLatest { repos ->
            if (repos != null) {
                repoDao.reposByIdsFlow(repos.map { repo -> repo.id })
            } else {
                flowOf(null)
            }
        }

    fun updateQuery(newQuery: String) {
        query.update { newQuery }
    }
}