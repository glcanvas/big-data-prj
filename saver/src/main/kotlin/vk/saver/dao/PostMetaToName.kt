package vk.saver.dao

import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id

@Entity("PostMetaDataToName")
data class PostMetaToName(
        @Id
        var wallId: Int,
        val wallName: String,
        val wallTitle: String,
)