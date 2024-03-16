package band.mlgb.ghmasta2.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id")
    val user_id: Long,
    val login: String,
    val avatar_url: String?,
    @SerialName("html_url")
    val user_html_url: String
) {
    fun asUnlikedEntity() =
        UserEntity(user_id, login, avatar_url, liked = false, html_url = user_html_url)

    fun asLikedEntity() =
        UserEntity(user_id, login, avatar_url, liked = true, html_url = user_html_url)
}