package vk.kafka.pojo

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import vk.kafka.utils.InstantDeserialize
import vk.kafka.utils.Typable
import java.time.Instant
import java.util.*

data class Post(
        var postId: Int,
        var wallId: Int,
        private var date: Instant,
        var text: String,
        var likes: Int,
        var reposts: Int,
        var views: Int,
        var images: List<String>
) : Typable {
    constructor() : this(0, 0, Instant.MIN, "", 0, 0, 0, Collections.emptyList())

    override fun type(): Typable.Type = Typable.Type.RESPONSE_POST

    @JsonDeserialize(using = InstantDeserialize::class)
    fun getDate(): Instant {
        return date
    }

    fun setDate(date: Instant) {
        this.date = date
    }

}