package vk.cron

import vk.cron.fault.VkApi
import vk.kafka.app.AbstractApp
import vk.kafka.pojo.RequestPost
import vk.kafka.utils.Typable
import vk.saver.DaoClient
import vk.saver.dao.PostMetaToName
import java.io.FileInputStream
import java.time.Instant
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit

class CronLauncher(bootstrap: String, private val vkKey: String, private val path: String, host: String, port: Int) : AbstractApp(bootstrap) {
    private val daoClient = DaoClient(host, port)
    private val vkApi = VkApi()

    override fun makeAction(fromKafka: BlockingQueue<Typable>, toKafka: BlockingQueue<Typable>) {
        fromKafka.poll()
        val properties = readProperty()
        for (key in properties.stringPropertyNames()) {
            val value = properties.getProperty(key)
            if (key.startsWith("wall")) {
                val response = vkApi.groupsApi().getGroups(vkKey, value).execute().body()
                if (response?.response == null || response.response.isEmpty()) {
                    println("${key}=${value} invalid")
                    continue
                }
                val wallId = -response.response[0].groupId
                val wallNameIdentifier = response.response[0].groupIdentifierName
                val wallName = response.response[0].groupName
                val meta = daoClient.getPostMeta(wallId)
                val time: Instant = if (meta.isEmpty()) {
                    Instant.MIN
                } else {
                    meta[0].lastTime
                }

                val metaLinkage = daoClient.getPostLinkage(wallId)
                if (metaLinkage.isEmpty()) {
                    daoClient.put(PostMetaToName(wallId, wallNameIdentifier, wallName))
                }

                toKafka.add(RequestPost(wallId, wallNameIdentifier, wallName, time))
            }
        }
        Thread.sleep(TimeUnit.SECONDS.toMillis(10))
    }

    override fun initialize() {
    }

    override fun appClose() {
        daoClient.close()
        vkApi.close()
    }

    private fun readProperty(): Properties {
        val p = Properties()
        FileInputStream(path).use {
            p.load(it)
        }
        println("Properties=$p")
        return p
    }
}