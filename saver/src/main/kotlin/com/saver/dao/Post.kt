package com.saver.dao

import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id
import java.time.Instant

@Entity("Post")
data class Post(
        @Id
        val postId: Int,
        val wallId: Int,
        val date: Instant,
        val text: String,
        val likes: Int,
        val reposts: Int,
        val views: Int,
        val images: List<ByteArray>
)
