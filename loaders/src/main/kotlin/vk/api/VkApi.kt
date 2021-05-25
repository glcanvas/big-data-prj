package vk.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import kotlin.system.exitProcess

class VkAPi : AutoCloseable {
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

fun main() {
    val vkApi = VkAPi()
    /*val call = vkApi.commentsApi().getComments("8b34623d8b34623d8b34623d128b595d3788b348b34623deb51e7e4149c9e0837beb45a",
            85635407,
            3199,
            0)
    println(call.execute().body())
     */

    val key = "8b34623d8b34623d8b34623d128b595d3788b348b34623deb51e7e4149c9e0837beb45a";
    /*val call2 = vkApi.postsApi().getPosts(key,
            -86529522,
            0)
*/
    val loader = VkPostLoader(key, -86529522, Instant.parse("2021-05-25T00:00:00.000Z"), Instant.parse("2030-01-01T00:00:00.000Z"))
    for (i in loader) {
        println(i)
    }
    //println(call2.execute().body())
    vkApi.close()
    exitProcess(0)
}

