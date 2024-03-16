package band.mlgb.ghmasta2.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "repos"
)
data class RepoEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val description: String?,
    val starred: Boolean,
    @Embedded
    val owner: User,
    val html_url: String
)