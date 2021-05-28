package vk.kafka.pojo

import org.apache.kafka.common.serialization.Serializer
import vk.kafka.mapper.Typable
import java.time.Instant
import java.util.*

data class Post(
        var postId: Int,
        var wallId: Int,
        var date: Instant,
        var text: String,
        var likes: Int,
        var reposts: Int,
        var views: Int,
        var images: List<ByteArray>
) : Typable {
    constructor(): this(0, 0, Instant.MIN, "", 0, 0, 0, Collections.emptyList())

    override fun type(): Typable.Type = Typable.Type.RESPONSE_POST

}