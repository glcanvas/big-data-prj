package vk.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import vk.comments.dao.CommentResponse
import vk.utils.BaseResponse

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
    ): Call<BaseResponse<CommentResponse>>
}