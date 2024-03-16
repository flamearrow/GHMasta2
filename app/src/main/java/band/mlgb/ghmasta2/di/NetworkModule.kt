package band.mlgb.ghmasta2.di

import band.mlgb.ghmasta2.network.GHSearchApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideOKHttpClientWithAuthBearer(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader(
                        "Authorization",
                        "Bearer MLGBRedacted"
                    )
                    .addHeader(
                        "ACCEPT",
                        "application/vnd.github+json" // as recommended by github
                    )
                    .build()
            )
        }.build()
    }

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
    }


    @Provides
    @Singleton
    fun provideGHSearchApi(
        client: OkHttpClient,
        json: Json
    ): GHSearchApi =
        Retrofit.Builder()
            .client(client)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .baseUrl("https://api.github.com")
            .build()
            .create(GHSearchApi::class.java)

}