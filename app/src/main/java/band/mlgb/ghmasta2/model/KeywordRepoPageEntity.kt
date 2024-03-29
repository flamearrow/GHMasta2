package band.mlgb.ghmasta2.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This can be merged with user_repo_page_entity by adding a new column, but who cares
 */
@Entity(
    tableName = "keyword_repo_page_entity"
)
class KeywordRepoPageEntity(
    @Embedded
    val repo: Repo,
    val nextPage: Int
) {
    @PrimaryKey(autoGenerate = true)
    var autoGeneratedId: Int = 0
}