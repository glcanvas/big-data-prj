package com.saver.dao


import dev.morphia.annotations.*
import java.time.Instant

@Entity("Comment")
@Indexes(Index(fields = [Field("commentId"), Field("wallId")], options = IndexOptions(unique = true)))
data class Comment(
        @Id
        var id: Int,
        var commentId: Int,
        var wallId: Int,
        var authorId: Int,
        var date: Instant,
        var text: String,
        val likes: Int,
        val images: List<ByteArray>
)