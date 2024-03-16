package band.mlgb.ghmasta2.database

import androidx.room.Database
import androidx.room.RoomDatabase
import band.mlgb.ghmasta2.model.KeywordRepoPageEntity
import band.mlgb.ghmasta2.model.RepoEntity
import band.mlgb.ghmasta2.model.UserEntity
import band.mlgb.ghmasta2.model.UserRepoPageEntity

@Database(
    entities = [UserEntity::class, RepoEntity::class, UserRepoPageEntity::class, KeywordRepoPageEntity::class],
    version = 1
)
abstract class GHMasta2DB : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun repoDao(): RepoDao

    abstract fun userRepoPageDao(): UserReposPageDao

    abstract fun keywordReposPageDao(): KeywordReposPageDao

    companion object {
        const val DB_NAME = "GHMASTA_2"
    }
}