package vk.kafka

import org.json.JSONObject
import vk.kafka.pojo.Post
import java.time.Instant

class Publisher {
    // private val a: KafkaProducer
    init {}

}

fun main() {
    val array = ByteArray(228)
    array[0] = 1
    val p = Post(1, 1, Instant.MIN, "dsd", 0, 0, 0, listOf(ByteArray(123)))
    val jo = JSONObject()
    jo.put("DD", JSONObject(p));

    println(jo)
}