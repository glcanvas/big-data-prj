package vk.cron.fault

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GroupsApi {
    @GET("method/groups.getById?v=5.130")
    fun getGroups(
            @Query("access_token") publicKey: String,
            @Query("group_id") groupId: String
    ): Call<ResponseHolder>
}