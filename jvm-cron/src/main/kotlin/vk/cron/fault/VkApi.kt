package vk.cron.fault

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class VkApi : AutoCloseable {
    private val httpClient: OkHttpClient = OkHttpClient()
    private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.vk.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    private val groupsApi: GroupsApi = retrofit.create(GroupsApi::class.java)


    fun groupsApi(): GroupsApi {
        return groupsApi
    }

    override fun close() {
        httpClient.connectionPool().evictAll()
        httpClient.dispatcher().executorService().shutdown()
    }

}
