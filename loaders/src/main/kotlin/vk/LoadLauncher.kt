package vk

import vk.api.ApiCaller
import vk.api.VkApi
import vk.api.VkLoader
import vk.dao.BaseResponse
import vk.dao.Comment
import vk.dao.Post
import vk.dao.ResponseHolder
import vk.kafka.app.AbstractApp
import vk.kafka.pojo.ObjectHolder
import vk.kafka.pojo.RequestComment
import vk.kafka.pojo.RequestPost
import vk.kafka.utils.DefaultOnTypeElement
import vk.kafka.utils.Typable
import vk.utils.Mapper
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

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
            for (p in loader) {
                sendQueue.add(mapper.map(p))
            }
        }

        override fun onRequestComment(item: RequestComment) {
            val commentCaller = object : ApiCaller<Comment> {
                override fun call(offset: Int, count: Int): ResponseHolder<BaseResponse<Comment>> {
                    return vkApi.commentsApi().getComments(key, item.wallId, item.postId, offset, count).execute().body()!!
                }
            }
            val loader = VkLoader(commentCaller, item.getLastTime())
            for (c in loader) {
                sendQueue.add(mapper.map(c, item.postId, item.wallId))
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

}