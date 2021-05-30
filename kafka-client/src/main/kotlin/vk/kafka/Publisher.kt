package vk.kafka

import com.google.common.collect.ImmutableMap
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.log4j.Logger
import vk.kafka.pojo.ObjectHolder
import vk.kafka.utils.ObjectHolderProcessor

class Publisher(bootstrapServer: String, private val topic: String) : AutoCloseable{
    private val log: Logger = Logger.getLogger(Publisher::class.java)
    private val producer = KafkaProducer(ImmutableMap.of<String?, Any?>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            bootstrapServer),
            StringSerializer(),
            ObjectHolderProcessor())

    init {
        log.debug("Publisher started: boostrap: $bootstrapServer, topi: $topic")
    }

    fun publish(data: ObjectHolder) {
        val future = producer.send(ProducerRecord(topic, "key", data))
        val res = future.get()
        log.debug("Message sent: data: $data, resp= $res")
    }

    override fun close() {
        producer.close()
    }
}