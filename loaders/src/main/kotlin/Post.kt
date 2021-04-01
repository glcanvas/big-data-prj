import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitByteArrayResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import java.time.Instant
import org.json.JSONObject

data class PostStrings(
        val id: Int,
        val date: Instant,
        val wallId: Int,
        val text: String,
        val likes: Int,
        val reposts: Int,
        val views: Int,
        val images: List<String>
)

data class PostBytes(
        val id: Int,
        val date: Instant,
        val wallId: Int,
        val text: String,
        val likes: Int,
        val reposts: Int,
        val views: Int,
        val images: List<ByteArray>
)


fun toPost(obj: JSONObject): PostStrings {
    val id = obj["id"] as Int
    val date = Instant.ofEpochSecond((obj["date"] as Int).toLong())
    val wallId = obj["owner_id"] as Int
    val text = obj["text"] as String
    val likes = (obj["likes"] as JSONObject)["count"] as Int
    val reposts = (obj["reposts"] as JSONObject)["count"] as Int
    val views = (obj["views"] as JSONObject)["count"] as Int
    val images: List<String> = if (obj.has("attachments")) {
        (obj["attachments"] as JSONArray).map { it as JSONObject }
                .filter { it["type"] == "photo" }
                .map { it["photo"] as JSONObject }
                .map { it["sizes"] as JSONArray }
                .filter { it.length() > 0 }
                .map { it.last() as JSONObject }
                .map { it["url"] as String }
    } else {
        listOf()
    }
    return PostStrings(id, date, wallId, text, likes, reposts, views, images)
}

suspend fun toPost(obj: PostStrings): PostBytes {
    val images: MutableList<ByteArray> = ArrayList()
    for (url in obj.images) {
        images.add(loadImage(url))
    }
    return PostBytes(obj.id, obj.date, obj.wallId, obj.text, obj.likes, obj.reposts, obj.views, images)
}

fun toPosts(posts: List<PostStrings>): List<PostBytes> {
    return runBlocking(Dispatchers.Default) {
        val result: MutableList<PostBytes> = ArrayList()
        for (post in posts) {
            result.add(toPost(post))
        }
        result
    }
}

private suspend fun loadImage(url: String): ByteArray {
    val result = Fuel.get(url).awaitByteArrayResult()
    return result.get()
}


