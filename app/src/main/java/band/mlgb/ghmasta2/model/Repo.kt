package band.mlgb.ghmasta2.model

import androidx.room.Embedded
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class Repo(
    val id: Long,
    val name: String,
    val description: String?,
    @Embedded
    val owner: User,
    val html_url: String
) {
    fun asStarredEntity() = RepoEntity(
        id, name, description, starred = true, owner, html_url = html_url
    )

    fun asUnstarredEntity() = RepoEntity(
        id, name, description, starred = false, owner, html_url = html_url
    )
}