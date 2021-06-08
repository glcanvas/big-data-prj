package vk.saver

import vk.kafka.app.AbstractApp
import vk.kafka.pojo.Comment
import vk.kafka.pojo.Post
import vk.kafka.utils.DefaultOnTypeElement
import vk.kafka.utils.Typable
import vk.saver.dao.CommentMetaData
import vk.saver.dao.PostMetaData
import vk.saver.utils.Mapper
import java.time.Instant
import java.util.*
import java.util.concurrent.BlockingQueue
import kotlin.collections.HashMap

class SaverLauncher(bootstrap: String, host: String, port: Int) : AbstractApp(bootstrap) {

    private val daoClient = DaoClient(host, port)

    private val wallLastTime: MutableMap<Int, Instant> = HashMap()
    private val commentsLastTime: MutableMap<Pair<Int, Int>, Instant> = HashMap()
    private val mapper = Mapper()

    private val onElement = object : DefaultOnTypeElement {
        override fun onPost(item: Post) {
            val mongoPost = mapper.map(item)
            if (!wallLastTime.containsKey(mongoPost.wallId)) {
                wallLastTime[mongoPost.wallId] = mongoPost.date
                daoClient.put(PostMetaData(item.wallId, mongoPost.date))
            }

            if (mongoPost.date >= wallLastTime[mongoPost.wallId]) {
                daoClient.put(mongoPost)
                daoClient.updateMetaPost(mongoPost.wallId, mongoPost.date)
                wallLastTime[mongoPost.wallId] = mongoPost.date
            }
        }

        override fun onComment(item: Comment) {
            val mongoComment = mapper.map(item)
            val key = Pair(mongoComment.wallId, mongoComment.postId)
            if (!commentsLastTime.containsKey(key)) {
                commentsLastTime[key] = mongoComment.date
                daoClient.put(CommentMetaData(UUID.randomUUID().toString(), item.wallId, item.postId, mongoComment.date))
            }

            if (mongoComment.date >= commentsLastTime[key]) {
                daoClient.put(mongoComment)
                daoClient.updateMetaComment(mongoComment.wallId, mongoComment.postId, mongoComment.date)
            }
        }
    }

    override fun makeAction(fromKafka: BlockingQueue<Typable>, toKafka: BlockingQueue<Typable>) {
        val item = fromKafka.poll(timeout_millis, time_out) ?: return
        onElement.onElement(item)
    }

    override fun initialize() {
        for (meta in daoClient.getPostMeta()) {
            wallLastTime[meta.wallId] = meta.lastTime
        }

        for (meta in daoClient.getCommentMeta()) {
            commentsLastTime[Pair(meta.wallId, meta.postId)] = meta.lastTime
        }
        println("initialize state: posts=$wallLastTime")
        println("initialize state: comments=${commentsLastTime.size}")
    }

    override fun appClose() {
        daoClient.close()
    }
}