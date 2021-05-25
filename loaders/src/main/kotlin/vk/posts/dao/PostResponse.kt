package vk.posts.dao

import vk.comments.dao.Profile

data class PostResponse(val count: Int,
                        val items: List<Post>,
                        val profiles: List<Profile>,
                        val groups: List<Group>)