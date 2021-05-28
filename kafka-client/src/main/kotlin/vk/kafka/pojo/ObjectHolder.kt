package vk.kafka.pojo


import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer
import vk.kafka.mapper.Typable
import java.lang.IllegalArgumentException

data class ObjectHolder(val t: Typable?) : Deserializer<ObjectHolder>, Serializer<ObjectHolder> {

    constructor() : this(null)

    override fun deserialize(topic: String?, data: ByteArray?): ObjectHolder {
        val rawString = String(data!!)
        val mapper = ObjectMapper()
        val json = mapper.readTree(rawString)
        val type = Typable.Type.resolve(json["type"].asText())
        val item = json["item"].toString()
        val processedItem = when (type) {
            Typable.Type.RESPONSE_POST -> mapper.readValue(item, Post::class.java)
            else -> throw IllegalArgumentException("Unexpected type: $type")
        }
        return ObjectHolder(processedItem)
    }

    override fun serialize(topic: String?, data: ObjectHolder?): ByteArray {
        val mapper = ObjectMapper()
        val node = mapper.createObjectNode()
        node.put("type", data!!.t!!.type().messageType)
        node.put("item", mapper.writeValueAsString(data.t!!))
        return node.toString().encodeToByteArray()
    }

    override fun close() {
    }

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {

    }
}
