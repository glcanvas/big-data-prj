package vk.kafka.utils


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer
import org.json.JSONObject
import vk.kafka.pojo.*
import java.lang.IllegalArgumentException

class ObjectHolderProcessor : Deserializer<ObjectHolder>, Serializer<ObjectHolder> {


    private val mapper: ObjectMapper = ObjectMapper()

    init {
        mapper.registerModule(JavaTimeModule())
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }

    override fun deserialize(topic: String?, data: ByteArray?): ObjectHolder {
        val rawString = String(data!!)
        val mapper = ObjectMapper()
        val json = mapper.readTree(rawString)
        val type = Typable.Type.resolve(json["type"].asText())
        val item = json["item"].textValue()
        val processedItem = when (type) {
            Typable.Type.RESPONSE_COMMENT -> mapper.readValue(item, Comment::class.java)
            Typable.Type.EXIT -> mapper.readValue(item, Exit::class.java)
            Typable.Type.REQUEST_COMMENT -> mapper.readValue(item, RequestComment::class.java)
            Typable.Type.RESPONSE_POST -> mapper.readValue(item, Post::class.java)
            Typable.Type.REQUEST_POST -> mapper.readValue(item, RequestPost::class.java)
            else -> throw IllegalArgumentException("Unexpected type: $type")
        }
        return ObjectHolder(processedItem)
    }

    override fun serialize(topic: String?, data: ObjectHolder?): ByteArray {
        val node = mapper.createObjectNode()
        node.put("type", data!!.t.type().messageType)
        node.put("item", mapper.writeValueAsString(data.t))
        return node.toString().encodeToByteArray()
    }

    override fun close() {
    }

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {

    }
}
