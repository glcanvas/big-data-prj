package vk.loader

import vk.api.VkLoader
import vk.kafka.app.AbstractApp
import vk.kafka.pojo.RequestComment
import vk.kafka.pojo.RequestPost
import vk.kafka.utils.DefaultOnTypeElement
import vk.kafka.utils.Typable
import vk.loader.api.ApiCaller
import vk.loader.api.VkApi
import vk.loader.dao.BaseResponse
import vk.loader.dao.Comment
import vk.loader.dao.Post
import vk.loader.dao.ResponseHolder
import vk.loader.utils.Mapper
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.function.Supplier

class LoadLauncher(bootstrap: String, key: String, imgPath: String) : AbstractApp(bootstrap) {

    private val vkApi = VkApi()

    private lateinit var sendQueue: BlockingQueue<Typable>

    private val worker = object : DefaultOnTypeElement {
        private val mapper = Mapper()
        override fun onRequestPost(item: RequestPost) {
            val postCaller = object : ApiCaller<Post> {
                override fun call(offset: Int, count: Int): ResponseHolder<BaseResponse<Post>> {
                    return vkApi.postsApi().getPosts(key, item.wallId, offset, count).execute().body()!!
                }
            }
            val loader = VkLoader(postCaller, item.getLastTime())
            var cnt = 0
            for (p in loader) {
                cnt++
                val res = errorFreeSupplier(p) { mapper.map(p, item.wallId, imgPath) }
                if (res != null) {
                    sendQueue.add(res)
                }
                if (cnt > 500) {
                    break
                }
            }
        }

        override fun onRequestComment(item: RequestComment) {
            val commentCaller = object : ApiCaller<Comment> {
                override fun call(offset: Int, count: Int): ResponseHolder<BaseResponse<Comment>> {
                    return vkApi.commentsApi().getComments(key, item.wallId, item.postId, offset, count).execute().body()!!
                }
            }
            val loader = VkLoader(commentCaller, item.getLastTime())
            var cnt = 0
            for (c in loader) {
                cnt++
                val res = errorFreeSupplier(c) { mapper.map(c, item.postId, item.wallId) }
                if (res != null) {
                    sendQueue.add(res)
                }
                if (cnt > 100) {
                    break
                }
            }
        }
    }

    override fun makeAction(fromKafka: BlockingQueue<Typable>, toKafka: BlockingQueue<Typable>) {
        val item = fromKafka.poll(timeout_millis, time_out) ?: return
        sendQueue = toKafka
        worker.onElement(item)
    }

    override fun initialize() {

    }

    override fun appClose() {
        vkApi.close()
    }

    private fun <V, T> errorFreeSupplier(v: V, s: Supplier<T>): T? {
        try {
            return s.get()
        } catch (t: Throwable) {
            print("Exception while process data $v")
            println(t)
            println(Arrays.toString(t.stackTrace))
            return null
        }
    }
}