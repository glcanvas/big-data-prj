import com.github.kittinunf.fuel.*;
import com.github.kittinunf.fuel.json.*;
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.util.*
import kotlin.math.min



// 0 -- 11 dec 2010
// 1 -- 5 dec 2010
open class VkFetcher constructor(private val publicKey: String,
                                 private val groupName: String,
                                 private val dateFrom: Instant) {

    companion object {
        private val apiVersion = Pair("v", "5.52")
        const val MAX_POSTS_IN_REQUEST = 100
    }

    fun fetchLocally(iterations: Int = 10): List<JSONObject> {
        var actualPostCount = fetchPostCount()
        var lastPostOffset = findOffsetByDate(dateFrom)
        var lastTime = dateFrom

        val data = LinkedList<JSONObject>()

        for (i in 1..iterations) {
            if (lastPostOffset < 0) {
                break
            }
            val (elements, postCount) = fetchPosts(MAX_POSTS_IN_REQUEST, lastPostOffset)
            val items = elements.map { x -> x as JSONObject }
            if (items.isEmpty()) {
                break
            }

            if (postCount == actualPostCount) {
                data.addAll(items)
                lastPostOffset -= items.size
                lastTime = Instant.ofEpochSecond((items[0]["date"] as Int).toLong())
            } else {
                val pair = syncUp(lastTime)
                lastPostOffset = pair.first
                actualPostCount = pair.second
            }
        }
        return data
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

        return min(r, fetchPostCount() - 1)
    }

    private fun fetchRawData(count: Int, offset: Int): JSONObject {
        val (_, _, c) = Fuel.get(
                "https://api.vk.com/method/wall.get",
                listOf(
                        Pair("domain", groupName),
                        Pair("access_token", publicKey),
                        Pair("count", count),
                        Pair("offset", offset),
                        apiVersion
                )
        ).responseString()
        return FuelJson(c.get()).obj()["response"] as JSONObject
    }
}


fun main() {

    val fetcher = VkFetcher("8b34623d8b34623d8b34623d128b595d3788b348b34623deb51e7e4149c9e0837beb45a",
            "sabatonclub",
            Instant.parse("2017-05-05T20:12:00.00Z"))

    val res = fetcher.fetchLocally(2)
    for (i in res) {
        print(i)
    }
    /*val fetcher = VkFetcher("8b34623d8b34623d8b34623d128b595d3788b348b34623deb51e7e4149c9e0837beb45a")
    var r = fetcher.findOffsetByDate("sabatonclub", Instant.ofEpochSecond(1291481911))
    System.out.println(r)

     */


    /*
val (a, b, c) = Fuel.get("https://baneks.site/tag/мужик/",
        listOf(Pair("p", 2))
).responseString()

System.out.println(c)
*/


}