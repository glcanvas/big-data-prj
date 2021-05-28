package vk.kafka

import com.google.common.collect.ImmutableMap
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import vk.kafka.pojo.ObjectHolder
import java.time.Duration
import java.util.*

class Consumer(bootstrapServer: String, topic: String) {
    private val consumer = KafkaConsumer(
            ImmutableMap.of<String, Any>(
                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                    bootstrapServer,
                    ConsumerConfig.GROUP_ID_CONFIG,
                    "tc-" + UUID.randomUUID(),
                    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                    "earliest"),
            StringDeserializer(),
            ObjectHolder())

    init {
        consumer.subscribe(Collections.singleton(topic))
    }

    fun fetch() {
        for (item in consumer.poll(Duration.ofSeconds(1))) {

        }
    }
}