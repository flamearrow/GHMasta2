package band.mlgb.ghmasta2.network

import band.mlgb.ghmasta2.model.Repo
import band.mlgb.ghmasta2.model.SearchRepoResponse
import band.mlgb.ghmasta2.model.User
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GHSearchApi {

    @GET(value = "users/{user_id}")
    fun searchUserAsFlow(@Path("user_id") userId: String): Flow<Response<User>>

    @GET(value = "users/{user_id}")
    suspend fun searchUser(@Path("user_id") userId: String): Response<User>

    /**
     * 404 if no such user
     */
    @GET(value = "users/{user_id}/repos")
    suspend fun searchUserRepos(
        @Path("user_id") userId: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = DEFAULT_ITEM_PER_PAGE,
    ): List<Repo>


    @GET(value = "search/repositories?sort=stars")
    suspend fun searchRepoWithKeyword(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = DEFAULT_ITEM_PER_PAGE,
    ): SearchRepoResponse

    companion object {
        const val DEFAULT_ITEM_PER_PAGE = 20
    }
}
