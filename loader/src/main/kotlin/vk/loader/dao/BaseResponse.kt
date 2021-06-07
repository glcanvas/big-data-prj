package vk.loader.dao

data class BaseResponse<T>(
        val count: Int,
        val items: List<T>,
        val profiles: List<Profile>,
        val groups: List<Group>
)
