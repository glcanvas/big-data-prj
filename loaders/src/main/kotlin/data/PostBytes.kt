package data

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitByteArrayResult
import java.time.Instant

data class PostBytes(
        val id: Int,
        val date: Instant,
        val wallId: Int,
        val text: String,
        val likes: Int,
        val reposts: Int,
        val views: Int,
        val images: List<ByteArray>
) {
    companion object {
        suspend fun toPost(obj: PostStrings): PostBytes {
            val images: MutableList<ByteArray> = ArrayList()
            for (url in obj.images) {
                images.add(loadImage(url))
            }
            return PostBytes(obj.id, obj.date, obj.wallId, obj.text, obj.likes, obj.reposts, obj.views, images)
        }


        private suspend fun loadImage(url: String): ByteArray {
            val result = Fuel.get(url).awaitByteArrayResult()
            return result.get()
        }


    }
}
