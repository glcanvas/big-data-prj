package vk.saver.utils

import vk.saver.dao.Comment
import vk.saver.dao.Post
import java.util.*

class Mapper {
    fun map(post: vk.kafka.pojo.Post): Post {
        val res = Post()
        res.id = UUID.randomUUID().toString()
        res.postId = post.postId
        res.wallId = post.wallId
        res.date = post.getDate()
        res.text = post.text
        res.likes = post.likes
        res.reposts = post.reposts
        res.views = post.views
        res.images = post.images
        return res
    }

    fun map(comment: vk.kafka.pojo.Comment): Comment {
        val res = Comment()
        res.id = UUID.randomUUID().toString()
        res.commentId = comment.commentId
        res.postId = comment.postId
        res.wallId = comment.wallId
        res.authorId = comment.authorId
        res.date = comment.getDate()
        res.text = comment.text
        res.likes = comment.likes
        res.images = comment.images
        return res
    }
}