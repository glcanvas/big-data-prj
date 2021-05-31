package vk.loader.dao

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import vk.loader.deserializers.CountToIntDeserializer
import vk.loader.deserializers.IntToInstantDeserializer
import vk.loader.deserializers.PathToUrlDeserializer
import java.time.Instant

data class Comment(
        @SerializedName("id")
        var commentId: Int,
        @SerializedName("from_id")
        var authorId: Int,
        @JsonAdapter(IntToInstantDeserializer::class)
        private var date: Instant,
        var text: String,
        @JsonAdapter(CountToIntDeserializer::class)
        val likes: Int,
        @SerializedName("attachments")
        @JsonAdapter(PathToUrlDeserializer::class)
        val images: List<String>
) : Datable {
    override fun getDate(): Instant {
        return date
    }
}