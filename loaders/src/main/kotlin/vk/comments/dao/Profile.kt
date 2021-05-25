package vk.comments.dao

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import vk.deserializers.IntToSexDeserializer

data class Profile(
        @SerializedName("first_name")
        val firstName: String,
        val id: Int,
        @JsonAdapter(value=IntToSexDeserializer::class)
        val sex: String,
        @SerializedName("photo_100")
        var photoUrl: String
)