@file:JvmName("Main")


package vk.loader

import vk.api.VkLoader
import vk.kafka.pojo.ObjectHolder
import vk.kafka.utils.ObjectHolderProcessor
import vk.launcher.Launcher
import vk.loader.api.ApiCaller
import vk.loader.api.VkApi
import vk.loader.dao.BaseResponse
import vk.loader.dao.Post
import vk.loader.dao.ResponseHolder
import vk.loader.utils.Mapper
import java.io.*
import java.nio.charset.StandardCharsets.UTF_16
import java.time.Instant
import java.util.*


fun main(args: Array<String>) {
    val envLauncher = Launcher.load()
    val launcher = LoadLauncher(envLauncher.getKafkaAddress(), envLauncher.getVkKey(), envLauncher.getImageDir())
    //val launcher = LoadLauncher("localhost:9092",
    //        "8b34623d8b34623d8b34623d128b595d3788b348b34623deb51e7e4149c9e0837beb45a",
    //        "/Users/nduginets/IdeaProjects/big-data-prj/images")
    val future = launcher.run()
    future.get()
}

/*
fun main() {
    val vkApi = VkApi()
    val wallId = -86529522
    val key = "8b34623d8b34623d8b34623d128b595d3788b348b34623deb51e7e4149c9e0837beb45a"
    val postCaller = object : ApiCaller<Post> {
        override fun call(offset: Int, count: Int): ResponseHolder<BaseResponse<Post>> {
            return vkApi.postsApi().getPosts(key, wallId, offset, count).execute().body()!!
        }
    }
    val loader = VkLoader(postCaller, Instant.MIN)
    for (v in loader) {
        println(v)
        val res = Mapper().map(v, 0, "/Users/nduginets/IdeaProjects/big-data-prj/images")

        var seril = ObjectHolderProcessor().serialize("t1", ObjectHolder(res))
        var desir = ObjectHolderProcessor().deserialize("t1", seril)
        println(desir)
        break
    }
}
 */