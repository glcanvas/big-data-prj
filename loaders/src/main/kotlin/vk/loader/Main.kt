package vk.loader

import vk.api.VkLoader
import vk.loader.api.ApiCaller
import vk.loader.api.VkApi
import vk.loader.dao.BaseResponse
import vk.loader.dao.Comment
import vk.loader.dao.Post
import vk.loader.dao.ResponseHolder
import java.time.Instant
import java.util.function.Supplier
import kotlin.system.exitProcess



fun main() {
    val vkApi = VkApi()
    /*val call = vkApi.commentsApi().getComments("8b34623d8b34623d8b34623d128b595d3788b348b34623deb51e7e4149c9e0837beb45a",
            85635407,
            3199,
            0)
    println(call.execute().body())
     */

    val key = "8b34623d8b34623d8b34623d128b595d3788b348b34623deb51e7e4149c9e0837beb45a";
    val wallId = -86529522
    val post = 442035
    /*val call2 = vkApi.postsApi().getPosts(key,
            -86529522,
            0)
*/

    val a = Runnable {
        val postCaller = object : ApiCaller<Post> {
            override fun call(offset: Int, count: Int): ResponseHolder<BaseResponse<Post>> {
                return vkApi.postsApi().getPosts(key, wallId, offset, count).execute().body()!!
            }
        }

        val loader = VkLoader(postCaller, Instant.parse("2021-05-25T00:00:00.000Z")) //, Instant.parse("2030-01-01T00:00:00.000Z"))
        for (i in loader) {
            println(i)
        }
    }

    a.run()
    println("==========================");

    val b = Runnable {
        val commentCaller = object : ApiCaller<Comment> {
            override fun call(offset: Int, count: Int): ResponseHolder<BaseResponse<Comment>> {
                return vkApi.commentsApi().getComments(key, wallId, post, offset, count).execute().body()!!
            }
        }

        val loader = VkLoader(commentCaller, Instant.parse("2021-05-25T00:00:00.000Z")) //, Instant.parse("2030-01-01T00:00:00.000Z"))
        for (i in loader) {
            println(i)
        }
    }
    b.run()
    vkApi.close()
    exitProcess(0)
}

