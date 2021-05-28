package vk.kafka

import com.google.common.collect.ImmutableMap
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import vk.kafka.pojo.ObjectHolder

class Publisher(bootstrapServer: String, private val topic: String) {
    private val producer = KafkaProducer(ImmutableMap.of<String?, Any?>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            bootstrapServer),
            StringSerializer(),
            ObjectHolder())

    fun publish(data: ObjectHolder) {
        val future = producer.send(ProducerRecord(topic, "key", data))
        future.get()
    }
}