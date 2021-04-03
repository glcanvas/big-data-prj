package data

import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant

data class PostStrings(
        val id: Int,
        val date: Instant,
        val wallId: Int,
        val text: String,
        val likes: Int,
        val reposts: Int,
        val views: Int,
        val images: List<String>
) {
    companion object {
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
    }
}