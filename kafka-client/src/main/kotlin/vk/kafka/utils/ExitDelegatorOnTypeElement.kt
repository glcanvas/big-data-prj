package vk.kafka.utils

import vk.kafka.pojo.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

class ExitDelegatorOnTypeElement(private val isExit: AtomicBoolean,
                                 private val blockingQueue: BlockingQueue<Typable>) : OnTypeElement {
    override fun onComment(item: Comment) {
        blockingQueue.add(item)
    }

    override fun onExit(item: Exit) {
        isExit.set(true)
    }

    override fun onPost(item: Post) {
        blockingQueue.add(item)
    }

    override fun onRequestComment(item: RequestComment) {
        blockingQueue.add(item)
    }

    override fun onRequestPost(item: RequestPost) {
        blockingQueue.add(item)
    }
}