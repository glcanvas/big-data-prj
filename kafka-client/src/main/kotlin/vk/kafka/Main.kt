package vk.kafka

import org.json.JSONObject
import vk.kafka.pojo.Post
import java.time.Instant

fun main() {
    val array = ByteArray(228)
    array[0] = 1
    val p = Post(1, 1, Instant.MIN, "dsd", 0, 0, 0, listOf())
    val jo = JSONObject()
    jo.put("DD", JSONObject(p))

    println(jo)
}