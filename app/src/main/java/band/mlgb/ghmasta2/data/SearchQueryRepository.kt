package band.mlgb.ghmasta2.data

import androidx.datastore.core.DataStore
import band.mlgb.ghmasta2.SearchQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Proto Datastore to save the current searchQuery
 */

class SearchQueryRepository @Inject constructor(
    val searchQueryDataStore: DataStore<SearchQuery>
) {
    val recentSearchQueries: Flow<List<String>> = searchQueryDataStore.data.map {
        it.recentSearchQueriesList
    }

    suspend fun addNewSearch(newSearchQuery: String) {
        searchQueryDataStore.updateData {
            it.toBuilder().addRecentSearchQueries(newSearchQuery).build()
        }
    }

    suspend fun clearRecentQueries() {
        searchQueryDataStore.updateData {
            it.toBuilder().clearRecentSearchQueries().build()
        }
    }

}