package band.mlgb.ghmasta2.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchRepoResponse(
    val total_count: Int,
    val items: List<Repo>
)