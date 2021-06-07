package vk.loader.deserializers

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.Instant

class IntToInstantDeserializer: TypeAdapter<Instant>() {
    override fun write(out: JsonWriter?, value: Instant?) {
        throw IllegalStateException("Unexpexted")
    }

    override fun read(reader: JsonReader?): Instant {
        return Instant.ofEpochSecond(reader!!.nextInt().toLong())
    }
}