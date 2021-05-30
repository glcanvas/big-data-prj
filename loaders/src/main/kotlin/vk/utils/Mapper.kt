package vk.utils

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import java.util.ArrayList
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class Mapper {

    private val loadService: ExecutorService = Executors.newFixedThreadPool(10)

    fun map(post: vk.dao.Post): vk.kafka.pojo.Post {
        val res = vk.kafka.pojo.Post()
        res.postId = post.postId
        res.wallId = post.wallId
        res.setDate(post.getDate())
        res.text = post.text
        res.likes = post.likes
        res.reposts = post.reposts
        res.views = post.views
        res.images = loadImages(post.images)
        return res
    }

    fun map(comment: vk.dao.Comment, postId: Int, wallId: Int): vk.kafka.pojo.Comment {
        val res = vk.kafka.pojo.Comment()
        res.commentId = comment.commentId
        res.postId = postId
        res.wallId = wallId
        res.authorId = comment.authorId
        res.setDate(comment.getDate())
        res.text = comment.text
        res.likes = comment.likes
        res.images = loadImages(comment.images)
        return res

    }


    private fun loadImages(urls: List<String>): List<ByteArray> {
        val futureList = ArrayList<Future<ByteArray?>>()
        for (url in urls) {
            val future = loadService.submit(Callable {
                val resp = Fuel.get(url).response()
                val exp = resp.third
                when (exp) {
                    is Result.Success<ByteArray> -> exp.value
                    is Result.Failure -> null
                }
            })
            futureList.add(future)
        }

        val resList = ArrayList<ByteArray>()
        for (f in futureList) {
            val res = f.get()
            if (res != null) {
                resList.add(res)
            }
        }
        return resList
    }
}