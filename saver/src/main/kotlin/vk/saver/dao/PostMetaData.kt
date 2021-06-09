package vk.saver.dao

import dev.morphia.annotations.*
import java.time.Instant

@Entity("PostMetaData")
data class PostMetaData(
        @Id
        var wallId: Int,
        var lastTime: Instant
)
{
        constructor() : this(0, Instant.MIN)
}