package vk.saver.dao


import dev.morphia.annotations.*
import java.time.Instant
import java.util.*

@Entity("Comment")
@Indexes(Index(fields = [Field("commentId"), Field("wallId"), Field("postId")],
        options = IndexOptions(unique = true)))
data class Comment(
        @Id
        var id: String,
        var commentId: Int,
        var postId: Int,
        var wallId: Int,
        var authorId: Int,
        var date: Instant,
        var text: String,
        var likes: Int,
        var images: List<ByteArray>
) {
        constructor() : this("", 0, 0, 0, 0, Instant.MIN, "", 0, Collections.emptyList())
}