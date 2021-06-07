package vk.loader.dao

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import vk.loader.deserializers.CountToIntDeserializer
import vk.loader.deserializers.PathToRowDataDeserializer
import vk.loader.deserializers.PathToUrlDeserializer
import vk.loader.deserializers.IntToInstantDeserializer
import java.time.Instant

data class Post(
        @SerializedName("id")
        val postId: Int,
        @JsonAdapter(IntToInstantDeserializer::class)
        private val date: Instant,
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
) : Datable {
    override fun getDate(): Instant {
        return date
    }
}
