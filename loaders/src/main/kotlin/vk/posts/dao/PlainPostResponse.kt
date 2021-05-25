package vk.posts.dao

import vk.comments.dao.Profile


data class PlainPostResponse(val count: Int,
                        val items: List<PostPlain>,
                        val profiles: List<Profile>,
                        val groups: List<Group>)