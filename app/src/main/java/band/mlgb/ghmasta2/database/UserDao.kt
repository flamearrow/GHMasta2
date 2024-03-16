package band.mlgb.ghmasta2.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import band.mlgb.ghmasta2.model.User
import band.mlgb.ghmasta2.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE liked=1")
    fun getAllLikedUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<UserEntity>

    @Delete
    fun delete(user: UserEntity)

    @Upsert
    suspend fun upsert(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    fun get(id: Long): Flow<UserEntity>

    @Upsert
    fun upsertAll(vararg users: UserEntity)

    @Query("SELECT IFNULL(liked, 0) FROM users WHERE id = :userId")
    suspend fun isLiked(userId: Long): Boolean

    @Query("DELETE FROM users")
    suspend fun clear()

    @Query("UPDATE users SET liked = 1 WHERE id = :userId")
    suspend fun like(userId: Long)

    @Query("UPDATE users SET liked = 0 WHERE id = :userId")
    suspend fun unlike(userId: Long)

    /**
     * If User is in memory, update its data with new User, keep is liked
     * Otherwise insert as unliked
     */
    suspend fun updateAndReturn(user: User): Flow<UserEntity> {
        if (isLiked(user.user_id)) {
            upsert(user.asLikedEntity())
        } else {
            upsert(user.asUnlikedEntity())
        }
        return get(user.user_id)
    }
}
