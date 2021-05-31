package vk.loader.dao

import com.google.gson.annotations.SerializedName

data class Group(
        val name: String,
        val id: Int,
        @SerializedName("photo_100")
        var photoUrl: String
)