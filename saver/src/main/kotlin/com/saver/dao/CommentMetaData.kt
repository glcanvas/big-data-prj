package com.saver.dao

import dev.morphia.annotations.*
import java.time.Instant

@Entity("CommentMetaData")
@Indexes(Index(fields = [Field("wallId"), Field("postId")], options = IndexOptions(unique = true)))
data class CommentMetaData(
        @Id
        var id: Int,
        var wallId: Int,
        var postId: Int,
        var lastTime: Instant
)
