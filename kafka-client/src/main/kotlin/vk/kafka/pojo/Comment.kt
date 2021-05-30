package vk.kafka.pojo

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import vk.kafka.utils.InstantDeserialize
import vk.kafka.utils.Typable
import java.time.Instant
import java.util.*

data class Comment(
        var commentId: Int,
        var postId: Int,
        var wallId: Int,
        var authorId: Int,
        private var date: Instant,
        var text: String,
        var likes: Int,
        var images: List<ByteArray>
) :Typable {
    constructor() : this(0, 0, 0, 0, Instant.MIN, "", 0, Collections.emptyList())

    override fun type(): Typable.Type = Typable.Type.RESPONSE_COMMENT

    @JsonDeserialize(using = InstantDeserialize::class)
    fun getDate(): Instant {
        return date
    }

    fun setDate(date: Instant) {
        this.date = date
    }
}

