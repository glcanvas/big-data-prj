package vk.kafka.app

import org.apache.log4j.Logger
import vk.kafka.Consumer
import vk.kafka.Publisher
import vk.kafka.pojo.*
import vk.kafka.utils.ExitDelegatorOnTypeElement
import vk.kafka.utils.OnTypeElement
import vk.kafka.utils.Typable
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean

abstract class AbstractApp(kafkaBootstrap: String) : AutoCloseable {
    protected val log: Logger = Logger.getLogger(AbstractApp::class.java)

    companion object {
        private const val topic = "t1"
        public const val timeout_millis = 100L
        public val time_out = TimeUnit.MILLISECONDS
    }

    private val publisher = Publisher(kafkaBootstrap, topic)
    private val consumer = Consumer(kafkaBootstrap, topic)

    private val isExit = AtomicBoolean(false)
    private val completed = CompletableFuture<Void>()
    private val fetchedQueue = LinkedBlockingQueue<Typable>()
    private val toProduceQueue = LinkedBlockingQueue<Typable>()

    private val threadPool = Executors.newFixedThreadPool(3)

    private val onExitElement = ExitDelegatorOnTypeElement(isExit, fetchedQueue)

    fun run(): Future<Void> {
        createTask {
            consumer.fetch(onExitElement)
        }
        createTask {
            val item = toProduceQueue.poll(timeout_millis, time_out)
            if (item != null) {
                publisher.publish(ObjectHolder(item))
            }
        }
        initialize()
        createTask { makeAction(fetchedQueue, toProduceQueue) }
        return completed
    }

    private fun createTask(r: Runnable) {
        threadPool.submit {
            while (!isExit.get()) {
                try {
                    r.run()
                } catch (i: InterruptedException) {
                    isExit.set(true)
                    Thread.currentThread().interrupt()
                    println("Interrupted occurred while sending data to kafka, stopping $i")
                    println(i)
                } catch (t: Throwable) {
                    println("Exception occurred while sending data to kafka $t")
                    println(t)
                }
            }
            completed.complete(null)
        }
    }

    protected abstract fun makeAction(fromKafka: BlockingQueue<Typable>, toKafka: BlockingQueue<Typable>)

    protected abstract fun initialize()

    protected abstract fun appClose()

    override fun close() {
        for (cl in listOf(Runnable { publisher.close() }, Runnable { consumer.close() }, Runnable { appClose() })) {
            try {
                cl.run()
            } catch (t: Throwable) {
                println("Exception occurred while closing resource $t")
                println(t)
            }
        }
        threadPool.shutdown()
        threadPool.shutdownNow()
    }
}