package band.mlgb.ghmasta2.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import band.mlgb.ghmasta2.model.Repo
import band.mlgb.ghmasta2.model.RepoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    @Query("SELECT * FROM repos WHERE starred=1")
    fun getAllStarredRepos(): Flow<List<RepoEntity>>

    @Query("SELECT * FROM repos")
    fun getAllRepos(): Flow<RepoEntity>

    @Query("SELECT * FROM repos WHERE user_id = :userId")
    fun getAllReposOwnedBy(userId: Long): Flow<RepoEntity>

    @Delete
    fun delete(user: RepoEntity)

    @Upsert
    suspend fun upsert(repo: RepoEntity)


    @Query("UPDATE repos SET starred = 1 WHERE id = :repoId")
    suspend fun star(repoId: Long)

    @Query("UPDATE repos SET starred = 0 WHERE id = :repoId")
    suspend fun unstar(repoId: Long)

    @Upsert
    fun upsertAll(vararg repos: RepoEntity)

    @Query("SELECT IFNULL(starred, 0) FROM repos WHERE id = :repoId")
    suspend fun isStarred(repoId: Long): Boolean

    @Query("SELECT * FROM repos WHERE id IN (:repoIds)")
    fun reposByIdsFlow(repoIds: List<Long>): Flow<List<RepoEntity>>


    @Query("SELECT * FROM repos WHERE login = :userName ORDER BY name ASC")
    fun repoOwnedByUserNamePagingSource(userName: String): PagingSource<Int, RepoEntity>

    @Query("SELECT * FROM repos WHERE name LIKE :repoKeyword ORDER BY name ASC")
    fun reposWithKeyword(repoKeyword: String): PagingSource<Int, RepoEntity>

    @Query("SELECT * FROM repos WHERE id IN (:repoIds)")
    suspend fun reposByIds(repoIds: List<Long>): List<RepoEntity>

    @Query("DELETE FROM repos")
    suspend fun clear()

    @Transaction
    suspend fun upsertRepos(repos: List<Repo>) {
        repos.forEach {
            if (isStarred(it.id)) {
                upsert(it.asStarredEntity())
            } else {
                upsert(it.asUnstarredEntity())
            }
        }
    }
}
