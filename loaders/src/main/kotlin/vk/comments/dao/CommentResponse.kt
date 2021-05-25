package vk.comments.dao

data class CommentResponse(
        val count: Int,
        val items: List<Comment>,
        val profiles: List<Profile>
)