package band.mlgb.ghmasta2.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import band.mlgb.ghmasta2.model.KeywordRepoPageEntity
import band.mlgb.ghmasta2.model.Repo

@Dao
interface KeywordReposPageDao {
    @Query("SELECT nextPage FROM keyword_repo_page_entity WHERE id=:repoId")
    suspend fun nextPageForRepo(repoId: Long): Int?

    @Upsert
    suspend fun insert(entity: KeywordRepoPageEntity)

    @Query("DELETE FROM keyword_repo_page_entity")
    suspend fun clear()


    @Transaction
    suspend fun updateKeys(repos: List<Repo>, nextPage: Int) {
        repos.forEach {
            this.insert(
                KeywordRepoPageEntity(it, nextPage)
            )
        }
    }
}