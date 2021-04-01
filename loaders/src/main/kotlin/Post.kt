import org.json.JSONArray
import java.time.Instant
import org.json.JSONObject

data class Post(
        val id: Int,
        val date: Instant,
        val wallId: Int,
        val text: String,
        val likes: Int,
        val reposts: Int,
        val views: Int,
        val images: List<String>
)


fun toPostMapper(obj: JSONObject): Post {
    val id = obj["id"] as Int
    val date = Instant.ofEpochSecond((obj["date"] as Int).toLong())
    val wallId = obj["owner_id"] as Int
    val text = obj["text"]
    val likes = (obj["likes"] as JSONObject)["count"] as Int
    val reposts = (obj["reposts"] as JSONObject)["count"] as Int
    val views = (obj["views"] as JSONObject)["count"] as Int
    val images = (obj["attachments"] as JSONArray).map { it as JSONObject }
            .filter { it["type"] == "photo" }
    return null!!
}
