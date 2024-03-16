package band.mlgb.ghmasta2.database

import android.util.Log
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import band.mlgb.ghmasta2.model.Repo
import band.mlgb.ghmasta2.model.UserRepoPageEntity

@Dao
interface UserReposPageDao {

    @Query("SELECT nextPage FROM user_repo_page_entity WHERE id=:repoId")
    suspend fun nextPageForRepo(repoId: Long): Int?

    @Upsert
    suspend fun inert(entity: UserRepoPageEntity)

    @Query("DELETE FROM user_repo_page_entity")
    suspend fun clear()

    @Transaction
    suspend fun updateKeys(repos: List<Repo>, nextPage: Int) {
        repos.forEach {
            this.inert(
                UserRepoPageEntity(
                    it,
                    nextPage
                )
            )
        }
    }
}