package com.saver.dao

import dev.morphia.annotations.*
import java.time.Instant
import java.util.*

@Entity("CommentMetaData")
@Indexes(Index(fields = [Field("wallId"), Field("postId")], options = IndexOptions(unique = true)))
data class CommentMetaData(
        @Id
        var id: Int,
        var wallId: Int,
        var postId: Int,
        var lastTime: Instant
) {
    constructor() : this(0, 0, 0, Instant.MIN)
}
