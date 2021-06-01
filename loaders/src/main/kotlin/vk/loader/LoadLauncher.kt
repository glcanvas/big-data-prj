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

class LoadLauncher(key: String, bootstrap: String) : AbstractApp(bootstrap) {

    private val vkApi = VkApi()

    private val sendQueue = LinkedBlockingQueue<Typable>()

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
                sendQueue.add(errorFreeSupplier(p) { mapper.map(p) })
                if(cnt > 100) {
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
                sendQueue.add(errorFreeSupplier(c) {mapper.map(c, item.postId, item.wallId)})
                if(cnt > 100) {
                    break
                }
            }
        }
    }

    override fun makeAction(fromKafka: BlockingQueue<Typable>, toKafka: BlockingQueue<Typable>) {
        val item = fromKafka.poll(timeout_millis, time_out) ?: return
        worker.onElement(item)
        while (sendQueue.isNotEmpty()) {
            toKafka.add(sendQueue.poll())
        }
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