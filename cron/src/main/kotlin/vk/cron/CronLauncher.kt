package vk.cron

import io.ktor.http.*
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
import io.ktor.server.netty.*
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.server.engine.*
import kotlin.collections.HashSet


class CronLauncher(bootstrap: String, private val vkKey: String, private val httpPort: Int,
                   host: String, port: Int) : AbstractApp(bootstrap) {
    private val daoClient = DaoClient(host, port)
    private val vkApi = VkApi()
    private lateinit var httpServer: NettyApplicationEngine

    private val groupSet: MutableSet<String> = Collections.synchronizedSet(HashSet())

    override fun makeAction(fromKafka: BlockingQueue<Typable>, toKafka: BlockingQueue<Typable>) {
        fromKafka.poll(timeout_millis, time_out)
        println("Groups: $groupSet")
        groupSet.add("pickabu")
        for (group in groupSet) {
            val response = vkApi.groupsApi().getGroups(vkKey, group).execute().body()
            if (response?.response == null || response.response.isEmpty()) {
                println("$group is invalid")
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
        Thread.sleep(TimeUnit.SECONDS.toMillis(10))
    }

    override fun initialize() {

        httpServer = embeddedServer(Netty, httpPort) {
            routing {
                get("/") {
                    call.respondText("Hello, world!", ContentType.Text.Html)
                }
                get("/list") {
                    call.respondText("Groups list: $groupSet", ContentType.Text.Html)
                }

                get("/add") {
                    groupSet.add("pickabu")
                    call.respondText("Groups list: $groupSet", ContentType.Text.Html)
                }
            }
        }.start()
    }

    override fun appClose() {
        daoClient.close()
        vkApi.close()
        httpServer.stop(5, 10, TimeUnit.SECONDS)
    }

}