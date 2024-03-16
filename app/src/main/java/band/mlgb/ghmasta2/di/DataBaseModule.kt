package band.mlgb.ghmasta2.di

import android.content.Context
import androidx.room.Room
import band.mlgb.ghmasta2.database.GHMasta2DB
import band.mlgb.ghmasta2.database.KeywordReposPageDao
import band.mlgb.ghmasta2.database.RepoDao
import band.mlgb.ghmasta2.database.UserDao
import band.mlgb.ghmasta2.database.UserReposPageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {
    @Provides
    @Singleton
    fun provideGHMasta2DB(@ApplicationContext context: Context): GHMasta2DB {
        return Room.databaseBuilder(context, GHMasta2DB::class.java, GHMasta2DB.DB_NAME).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(db: GHMasta2DB): UserDao = db.userDao()

    @Provides
    @Singleton
    fun provideRepoDao(db: GHMasta2DB): RepoDao = db.repoDao()

    @Provides
    @Singleton
    fun provideUserRepoPageDao(db: GHMasta2DB): UserReposPageDao = db.userRepoPageDao()

    @Provides
    @Singleton
    fun provideKeywordReposPageDao(db: GHMasta2DB): KeywordReposPageDao = db.keywordReposPageDao()
}