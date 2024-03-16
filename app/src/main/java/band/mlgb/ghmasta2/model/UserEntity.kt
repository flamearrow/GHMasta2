package band.mlgb.ghmasta2.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "users"
)
class UserEntity(
    @PrimaryKey
    val id: Long,
    val login: String,
    val avatar_url: String?,
    val liked: Boolean,
    val html_url: String
)