package vk.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.reflect.Type

class IntToSexDeserializer(): TypeAdapter<String>() {
    override fun write(out: JsonWriter?, value: String?) {
        throw IllegalStateException("unexpected state write")
    }

    override fun read(reader: JsonReader?): String {
        val sex = reader!!.nextInt()
        when(sex) {
            0 -> return "unknown"
            1 -> return "female"
            2 -> return "male"
            else -> throw IllegalStateException("Unknown gender $sex")
        }
    }

}