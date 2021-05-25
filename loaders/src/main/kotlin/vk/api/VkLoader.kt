package vk.api

import org.apache.log4j.Logger
import vk.posts.dao.PostPlain
import java.lang.IllegalStateException
import java.time.Instant
import java.util.stream.Collector
import java.util.stream.Collectors
import java.util.stream.Collectors.toList
import kotlin.math.log
import kotlin.math.max
import kotlin.math.min

class VkPostLoader(private val accessKey: String,
                   private val wallId: Int, val from: Instant, val to: Instant) : Iterable<PostPlain> {
    private val logger: Logger = Logger.getLogger(VkPostLoader::class.java)
    private val postApi = VkAPi().postsApi()
    private val postOffset: Int

    init {
        logger.info("start with arguments: wallId=$wallId, from=$from, to=$to")
        postOffset = initializeOffset()
    }


    private fun initializeOffset(): Int {
        val count = postApi.getPlainPosts(accessKey, wallId, Int.MAX_VALUE, 1).execute().body()!!.response.count
        var r = count
        var l = -1
        while (l < r - 1) {
            val m = (r + l) / 2
            val response = postApi.getPlainPosts(accessKey, wallId, m, 1).execute().body()!!.response
            val items = response.items
            if (items.isEmpty()) {
                r = m
            } else {
                val post = items[0]
                val date = post.date
                if (date <= from) {
                    r = m
                } else {
                    l = m
                }
            }
        }
        val offset = min(l, count - 1)
        logger.info("start with offset: $offset")
        return offset
    }

    override fun iterator(): Iterator<PostPlain> {
        return VkLoadIterator(postApi, accessKey, wallId, from, to, postOffset)
    }


    private class VkLoadIterator(private val postApi: PostsApi,
                                 private val accessKey: String,
                                 private val wallId: Int,
                                 private val from: Instant,
                                 private val to: Instant,
                                 private var offset: Int) : Iterator<PostPlain> {
        val MAX_BATCH_SIZE = 100
        val queue = ArrayDeque<PostPlain>()

        override fun hasNext(): Boolean {
            return offset != 0 || !queue.isEmpty()
        }

        override fun next(): PostPlain {
            if (queue.isEmpty() && offset < 0) {
                throw IllegalStateException("Iterator finished")
            }
            if (queue.isEmpty()) {
                offset = max(0, offset - MAX_BATCH_SIZE)
                val posts: List<PostPlain> = postApi.getPlainPosts(accessKey, wallId, offset, MAX_BATCH_SIZE).execute().body()!!.response.items
                        .stream()
                        .filter { it.date in from..to }
                        .collect(toList())
                queue.addAll(posts)
            }
            return queue.removeFirst()
        }
    }


}