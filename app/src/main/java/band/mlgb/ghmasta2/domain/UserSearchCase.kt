package band.mlgb.ghmasta2.domain

import band.mlgb.ghmasta2.database.UserDao
import band.mlgb.ghmasta2.model.UserEntity
import band.mlgb.ghmasta2.network.GHSearchApi
import band.mlgb.ghmasta2.network.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Search user, upsert into DB and return from DB
 */
class UserSearchCase @Inject constructor(
    val ghSearchApi: GHSearchApi,
    val userDao: UserDao
) {

//    @OptIn(ExperimentalCoroutinesApi::class)
//    operator fun invoke(userId: String) = ghSearchApi.searchUser(userId).flatMapLatest {
//        userDao.updateAndReturn(it)
//    }


    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(userId: String): Flow<Resource<UserEntity?>> =
        flowOf(Resource.Loading<UserEntity>()).flatMapLatest {
            flow {
                emit(ghSearchApi.searchUser(userId))
                // can also return flow directly from gh
                // ghSearchApi.searchUserAsFlow
            }
        }.flatMapLatest {
            if (it.isSuccessful) {
                userDao.updateAndReturn(requireNotNull(it.body())).map { userEntity ->
                    Resource.Result(userEntity)
                }
            } else {
                if (it.code() == 404) {
                    flowOf(Resource.Result(null))
                } else {
                    flowOf(Resource.Error(it.message()))
                }
            }
        }

}