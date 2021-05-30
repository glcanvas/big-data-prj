package vk.kafka.pojo

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import vk.kafka.utils.InstantDeserialize
import vk.kafka.utils.Typable
import java.time.Instant

data class RequestComment(var wallId: Int,
                          var postId: Int,
                          private var lastTime: Instant
) : Typable {
    constructor() : this(0, 0, Instant.MIN)
    override fun type(): Typable.Type = Typable.Type.REQUEST_COMMENT

    @JsonDeserialize(using = InstantDeserialize::class)
    fun getLastTime(): Instant {
        return lastTime
    }

    fun setLastTime(date: Instant) {
        this.lastTime = date
    }
}
