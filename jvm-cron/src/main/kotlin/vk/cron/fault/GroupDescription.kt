package vk.cron.fault

import com.google.gson.annotations.SerializedName

data class GroupDescription(
        @SerializedName("id")
        val groupId: Int,
        @SerializedName("screen_name")
        val groupIdentifierName: String,
        @SerializedName("name")
        val groupName: String
)
