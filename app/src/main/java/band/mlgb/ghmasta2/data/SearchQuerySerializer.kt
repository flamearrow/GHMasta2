package band.mlgb.ghmasta2.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import band.mlgb.ghmasta2.SearchQuery
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class SearchQuerySerializer @Inject constructor() : Serializer<SearchQuery> {
    override val defaultValue: SearchQuery = SearchQuery.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SearchQuery {
        try {
            return SearchQuery.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: SearchQuery,
        output: OutputStream
    ) = t.writeTo(output)
}

// This works, but overengineering by providing through a module
//val Context.searchQueryDataStore: DataStore<SearchQuery> by dataStore(
//    fileName = "search_pref.pb",
//    serializer = SearchQuerySerializer()
//)
