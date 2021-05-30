package vk.kafka.utils

import vk.kafka.pojo.*
import java.lang.IllegalArgumentException

interface OnTypeElement {

    fun onElement(item: Typable) {
        when (item.type()) {
            Typable.Type.RESPONSE_COMMENT -> onComment(item as Comment)
            Typable.Type.EXIT -> onExit(item as Exit)
            Typable.Type.REQUEST_COMMENT -> onRequestComment(item as RequestComment)
            Typable.Type.RESPONSE_POST -> onPost(item as Post)
            Typable.Type.REQUEST_POST -> onRequestPost(item as RequestPost)
            else -> throw IllegalArgumentException("Unknown type ${item.type()}")
        }
    }

    fun onComment(item: Comment)
    fun onExit(item: Exit)
    fun onPost(item: Post)
    fun onRequestComment(item: RequestComment)
    fun onRequestPost(item: RequestPost)
}