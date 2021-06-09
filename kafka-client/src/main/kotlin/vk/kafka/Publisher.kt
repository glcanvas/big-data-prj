package vk.kafka

import com.google.common.collect.ImmutableMap
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import vk.kafka.pojo.ObjectHolder
import vk.kafka.utils.ObjectHolderProcessor

class Publisher(bootstrapServer: String, private val topic: String) : AutoCloseable{
    private val producer = KafkaProducer(ImmutableMap.of<String?, Any?>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            bootstrapServer),
            StringSerializer(),
            ObjectHolderProcessor())

    init {
        println("Publisher started: boostrap: $bootstrapServer, topi: $topic")
    }

    fun publish(data: ObjectHolder) {
        val future = producer.send(ProducerRecord(topic, "key", data))
        val res = future.get()
        println("Message sent: data: $data, resp= $res")
    }

    override fun close() {
        producer.close()
    }
}

/*
fun main() {
    var prod = KafkaProducer(ImmutableMap.of<String?, Any?>(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            "localhost:9092",
    ),

            StringSerializer(),
            StringSerializer()) // KAFKA_ADVERTISED_LISTENERS

    var fut = prod.send(ProducerRecord("sosalovo", "kek", "mek"))
    val res = fut.get()
    println(res)

}

 */