package vk.loader.deserializers

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
        try {
            val listString = ArrayList<String>()
            for (i in json!!.asJsonArray) {
                var item = i.asJsonObject
                if (item["type"].asString != "photo") {
                    continue
                }
                item = item["photo"].asJsonObject
                val photos = item["sizes"].asJsonArray

                val photoItems: MutableList<Pair<Int, String>> = ArrayList()
                for (p in photos) {
                    val photo = p.asJsonObject
                    val sq = photo["height"].asInt * photo["width"].asInt
                    val maxPixel = sq
                    val maxLink = photo["url"].asString
                    photoItems.add(Pair(maxPixel, maxLink))
                }
                photoItems.sortWith(Comparator.comparingInt { it.first })
                val imgIdx = if (photoItems.size - 4 < 0) {
                    photoItems.size - 1
                } else {
                    photoItems.size - 4
                }
                if (photoItems.isNotEmpty()) {
                    listString.add(photoItems[imgIdx].second)
                }
            }
            return listString
        } catch (t: Throwable) {
            println(")")
            return emptyList()
        }
    }
}