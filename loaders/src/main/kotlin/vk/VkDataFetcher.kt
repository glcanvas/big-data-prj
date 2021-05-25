package vk

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.FuelJson
import data.PostStrings
import data.VkProperties
import org.apache.log4j.Logger
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.util.*

class VkDataFetcher constructor(vkProperties: VkProperties) {
    private val logger: Logger = Logger.getLogger(VkDataFetcher::class.java)

    private var dateFrom = vkProperties.dateFrom
    private val groupName = vkProperties.groupName
    private val publicKey = vkProperties.publicKey

    companion object {
        private val apiVersion = Pair("v", "5.130")
        const val MAX_POSTS_IN_REQUEST = 100
    }

    fun safeFetchLocally(iterations: Int = 10): Pair<Instant, List<PostStrings>> {
        return try {
            fetchLocally(iterations)
        } catch (e: Exception) {
            Pair(Instant.MIN, listOf())
        }
    }

    private fun fetchLocally(iterations: Int = 10): Pair<Instant, List<PostStrings>> {
        var actualPostCount = fetchPostCount()
        var lastPostOffset = findOffsetByDate(dateFrom)
        val data = ArrayList<JSONObject>()
        logger.info("$groupName, begin, actualPostCount: $actualPostCount, lastPostOffset: $lastPostOffset")
        for (i in 1..iterations) {
            lastPostOffset -= Math.min(lastPostOffset, MAX_POSTS_IN_REQUEST) // well its required for moving dateFrom pointer
            if (lastPostOffset < 0) {
                break
            }
            val (elements, postCount) = fetchPosts(MAX_POSTS_IN_REQUEST, lastPostOffset)
            val items = elements.map { x -> x as JSONObject }
            if (items.isEmpty()) {
                break
            }
            logger.info("$groupName, fetched ${items.size} elements")
            if (postCount == actualPostCount) {
                data.addAll(items)
                lastPostOffset -= items.size
                dateFrom = Instant.ofEpochSecond((items[0]["date"] as Int).toLong())
                logger.info("$groupName, fetched data correctly, dateFrom: $dateFrom, lastPostOffset: $lastPostOffset")
            } else {
                val pair = syncUp(dateFrom)
                lastPostOffset = pair.first
                actualPostCount = pair.second
                logger.info("$groupName, posts count changed, lastPostOffset: $lastPostOffset, actualPostCount: $actualPostCount")
            }
        }
        logger.info("$groupName, finished, date: $dateFrom")
        return Pair(dateFrom, data.map { PostStrings.toPost(it) })
    }

    private fun syncUp(lastTime: Instant): Pair<Int, Int> {
        val offset = findOffsetByDate(lastTime)
        val counts = fetchPostCount()
        return Pair(offset, counts)
    }

    private fun fetchPosts(count: Int, offset: Int): Pair<JSONArray, Int> {
        val response = fetchRawData(count, offset)
        val postsCount = response["count"] as Int
        return Pair(response["items"] as JSONArray, postsCount)
    }

    private fun fetchPostCount(): Int {
        val response = fetchRawData(1, 0)
        return response["count"] as Int
    }

    private fun findOffsetByDate(time: Instant): Int {
        var l = -1
        var r = fetchPostCount()

        while (l < r - 1) {
            val m = (l + r) / 2
            val items = fetchPosts(1, m)
            if (items.first.length() == 0) {
                r = m
            } else {
                val post = items.first[0] as JSONObject
                val date = post["date"] as Int
                val postTime = Instant.ofEpochSecond(date.toLong())
                if (postTime <= time) {
                    r = m
                } else {
                    l = m
                }
            }
        }

        return Math.min(r, fetchPostCount() - 1)
    }

    private fun fetchRawData(count: Int, offset: Int): JSONObject {
        val request = Fuel.get(
                "https://api.vk.com/method/wall.get",
                listOf(
                        Pair("domain", groupName),
                        Pair("access_token", publicKey),
                        Pair("count", count),
                        Pair("offset", offset),
                        apiVersion
                )
        )
        val (_, _, c) = request.responseString()
        try {
            return FuelJson(c.get()).obj()["response"] as JSONObject
        } catch(e: Exception) {
            logger.error("Exception: $e, request: $request, response: $c")
            throw e
        }
    }
}
