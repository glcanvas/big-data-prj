package vk.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import kotlin.system.exitProcess

class VkApi : AutoCloseable {
    private val httpClient: OkHttpClient = OkHttpClient()
    private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.vk.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    private val commentsApi: CommentsApi = retrofit.create(CommentsApi::class.java)
    private val postsApi: PostsApi = retrofit.create(PostsApi::class.java)

    fun commentsApi(): CommentsApi {
        return commentsApi
    }

    fun postsApi(): PostsApi {
        return postsApi
    }

    override fun close() {
        httpClient.connectionPool().evictAll()
        httpClient.dispatcher().executorService().shutdown()
    }

}
