package vk.loader.dao

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import vk.loader.deserializers.IntToSexDeserializer

data class Profile(
        @SerializedName("first_name")
        val firstName: String,
        val id: Int,
        @JsonAdapter(value= IntToSexDeserializer::class)
        val sex: String,
        @SerializedName("photo_100")
        var photoUrl: String
)