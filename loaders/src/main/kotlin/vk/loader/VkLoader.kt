package vk.api

import org.apache.log4j.Logger
import vk.loader.api.ApiCaller
import vk.loader.dao.Datable
import java.lang.IllegalStateException
import java.time.Instant
import java.util.*
import java.util.stream.Collectors.toList
import kotlin.collections.ArrayDeque
import kotlin.math.min

class VkLoader<T : Datable>(private val apiCaller: ApiCaller<T>,
                            private val from: Instant) : Iterable<T> {
    private val logger: Logger = Logger.getLogger(VkLoader::class.java)
    private val postOffset: Int

    init {
        logger.info("Starts with arguments: apiCaller=$apiCaller, from=$from")
        postOffset = initializeOffset()
    }

    private fun initializeOffset(): Int {
        val defResponse = apiCaller.call(Int.MAX_VALUE, 1).response ?: return -1
        val count = defResponse.count
        var r = count
        var l = -1
        while (l < r - 1) {
            val m = (r + l) / 2
            val response = apiCaller.call(m, 1).response ?: return -1
            val items = response.items
            if (items.isEmpty()) {
                r = m
            } else {
                val post = items[0]
                val date = post.getDate()
                if (date <= from) {
                    r = m
                } else {
                    l = m
                }
            }
        }
        val offset = min(l, count - 1)
        logger.info("Starts with offset: $offset")
        return offset
    }

    override fun iterator(): Iterator<T> {
        return VkLoadIterator(apiCaller, from, postOffset)
    }


    private class VkLoadIterator<T : Datable>(private val apiCaller: ApiCaller<T>,
                                              private val from: Instant,
                                              private var offset: Int) : Iterator<T> {
        val MAX_BATCH_SIZE = 100
        val queue = ArrayDeque<T>()

        override fun hasNext(): Boolean {
            return offset > 0 || !queue.isEmpty()
        }

        override fun next(): T {
            if (queue.isEmpty() && offset < 0) {
                throw IllegalStateException("Iterator finished")
            }
            if (queue.isEmpty()) {
                offset -= MAX_BATCH_SIZE
                var cnt = MAX_BATCH_SIZE
                if (offset < 0) {
                    offset = 0
                    cnt = Math.abs(offset)
                }
                val posts: List<T> = apiCaller.call(offset, cnt).response?.items ?: Collections.emptyList()
                posts
                        .stream()
                        .filter { it.getDate() > from }
                        .collect(toList())
                queue.addAll(posts)
            }
            return queue.removeFirst()
        }
    }


}