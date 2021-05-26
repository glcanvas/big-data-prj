package com.saver.dao

import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id
import java.time.Instant

@Entity("PostMetaData")
data class PostMetaData(
        @Id
        var wallId: Int,
        var name: String,
        var lastTime: Instant
)
