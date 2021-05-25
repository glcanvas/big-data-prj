package vk.posts.dao

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import vk.deserializers.CountToIntDeserializer
import vk.deserializers.IntToInstantDeserializer
import vk.deserializers.PathToUrlDeserializer
import java.time.Instant

data class PostPlain(
        val id: Int,
        @JsonAdapter(IntToInstantDeserializer::class)
        val date: Instant,
        val wallId: Int,
        val text: String,
        @JsonAdapter(CountToIntDeserializer::class)
        val likes: Int,
        @JsonAdapter(CountToIntDeserializer::class)
        val reposts: Int,
        @JsonAdapter(CountToIntDeserializer::class)
        val views: Int,
        @SerializedName("attachments")
        @JsonAdapter(PathToUrlDeserializer::class)
        val images: List<String>
)
