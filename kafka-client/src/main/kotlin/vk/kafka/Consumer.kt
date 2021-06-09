package vk.kafka

import com.google.common.collect.ImmutableMap
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import vk.kafka.utils.ObjectHolderProcessor
import vk.kafka.utils.OnTypeElement
import java.io.Closeable
import java.time.Duration
import java.util.*

class Consumer(bootstrapServer: String, topic: String): AutoCloseable {
    private val consumer = KafkaConsumer(
            ImmutableMap.of<String, Any>(
                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                    bootstrapServer,
                    ConsumerConfig.GROUP_ID_CONFIG,
                    "tc-" + UUID.randomUUID(),
                    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                    "earliest"),
            StringDeserializer(),
            ObjectHolderProcessor())

    init {
        consumer.subscribe(Collections.singleton(topic))
        println("Consumer started: bootstrap: $bootstrapServer, topic: $topic")
    }

    fun fetch(callable: OnTypeElement): Int {
        var cnt = 0
        for (item in consumer.poll(Duration.ofSeconds(1))) {
            callable.onElement(item.value().t)
            println("Message processed: key= ${item.key()}, value=${item.value()}")
            cnt++
        }
        println("Fetched messages: $cnt")
        return cnt
    }

    override fun close() {
        consumer.close()
    }
}