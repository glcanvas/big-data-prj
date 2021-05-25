package vk.comments.dao

import com.google.gson.annotations.JsonAdapter
import vk.deserializers.IntToInstantDeserializer
import java.time.Instant

data class Comment(
        var id: Int,
        var from_id: Int,
        @JsonAdapter(IntToInstantDeserializer::class)
        var date: Instant,
        var text: String,
        var likes: Likes
)
