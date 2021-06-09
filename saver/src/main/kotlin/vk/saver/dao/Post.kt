package vk.saver.dao

import dev.morphia.annotations.*
import java.time.Instant
import java.util.*

@Entity("Post")
@Indexes(Index(fields = [Field("postId"), Field("wallId")],
        options = IndexOptions(unique = true)))
data class Post(
        @Id
        var id: String,
        var postId: Int,
        var wallId: Int,
        var date: Instant,
        var text: String,
        var likes: Int,
        var reposts: Int,
        var views: Int,
        var images: List<ByteArray>
) {
    constructor() : this("", 0, 0, Instant.MIN, "", 0, 0, 0, Collections.emptyList())
}
