package vk.kafka.utils

import vk.kafka.pojo.*

interface DefaultOnTypeElement : OnTypeElement {

    override fun onComment(item: Comment) {}
    override fun onPost(item: Post) {}
    override fun onExit(item: Exit) {}
    override fun onRequestComment(item: RequestComment) {}
    override fun onRequestPost(item: RequestPost) {}
}