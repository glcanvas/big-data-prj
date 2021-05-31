package vk.loader.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import vk.loader.dao.BaseResponse
import vk.loader.dao.Comment
import vk.loader.dao.ResponseHolder

interface CommentsApi {

    @GET("method/wall.getComments?v=5.130")
    fun getComments(
            @Query("access_token") publicKey: String,
            @Query("owner_id") ownerId: Int,
            @Query("post_id") postId: Int,
            @Query("offset") offset: Int,
            @Query("count") count: Int = 100,
            @Query("need_likes") needLikes: Int = 1,
            @Query("sort") sort: String = "asc",
            @Query("preview_length") previewLength: Int = 0,
            @Query("extended") extended: Int = 1
    ): Call<ResponseHolder<BaseResponse<Comment>>>
}