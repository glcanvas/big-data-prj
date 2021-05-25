package vk.deserializers

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.Future
import java.util.stream.Collectors
import kotlin.collections.ArrayList

class PathToRowDataDeserializer : JsonDeserializer<List<ByteArray>> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): List<ByteArray> {
        val listString = ArrayList<String>()
        for (i in json!!.asJsonArray) {
            var item = i.asJsonObject
            if (item["type"].asString != "photo") {
                continue
            }
            item = item["photo"].asJsonObject
            val photos = item["sizes"].asJsonArray
            var maxPixel = Int.MAX_VALUE
            var maxLink: String? = null
            for (p in photos) {
                val photo = p.asJsonObject
                val sq = photo["height"].asInt * photo["width"].asInt
                if (sq < maxPixel) {
                    maxPixel = sq
                    maxLink = photo["url"].asString
                }
            }
            listString.add(maxLink!!)
        }
        val bytesList = ArrayList<Optional<ByteArray>>()
        val futureList = ArrayList<Future<Response>>()
        for (i in 0 until listString.size) {
            bytesList.add(Optional.empty())
            val future = Fuel.get(listString[i]).response { it ->
                when (it) {
                    is Result.Failure -> throw it.getException()
                    is Result.Success -> bytesList[i] = Optional.of(it.value)
                }
            }
            futureList.add(future)
        }
        for (future in futureList) {
            future.get()
        }
        return bytesList.stream().filter { x -> x.isPresent }.map { x -> x.get() }.collect(Collectors.toList())
    }
}