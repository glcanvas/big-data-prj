package vk.dao

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import vk.deserializers.CountToIntDeserializer
import vk.deserializers.IntToInstantDeserializer
import vk.deserializers.PathToUrlDeserializer
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