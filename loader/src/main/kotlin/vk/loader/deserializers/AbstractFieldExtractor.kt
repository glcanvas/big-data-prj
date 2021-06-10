package vk.loader.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

abstract class AbstractFieldExtractor : JsonDeserializer<Int> {

    abstract fun field(): String

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Int {
        return json!!.asJsonObject[field()].asInt
    }
}