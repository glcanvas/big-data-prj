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

class PathToUrlDeserializer : JsonDeserializer<List<String>> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): List<String> {
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
        return listString
    }
}