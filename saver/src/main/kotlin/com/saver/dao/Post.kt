package com.saver.dao

import dev.morphia.annotations.*
import java.time.Instant
import java.util.*

@Entity("Post")
data class Post(
        @Id
        var postId: Int,
        var wallId: Int,
        var date: Instant,
        var text: String,
        var likes: Int,
        var reposts: Int,
        var views: Int,
        var images: List<ByteArray>
) {
        constructor() : this(0, 0, Instant.MIN, "", 0, 0, 0, Collections.emptyList())
}
