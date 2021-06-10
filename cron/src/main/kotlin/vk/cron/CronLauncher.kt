package vk.cron

import io.ktor.http.*
import vk.cron.fault.VkApi
import vk.kafka.app.AbstractApp
import vk.kafka.pojo.RequestPost
import vk.kafka.utils.Typable
import vk.saver.DaoClient
import vk.saver.dao.PostMetaToName
import java.time.Instant
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import io.ktor.server.netty.*
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.server.engine.*
import vk.kafka.pojo.Exit
import kotlin.collections.HashSet


class CronLauncher(bootstrap: String, private val vkKey: String, private val httpPort: Int,
                   host: String, port: Int) : AbstractApp(bootstrap) {
    private val daoClient = DaoClient(host, port)
    private val vkApi = VkApi()
    private lateinit var httpServer: NettyApplicationEngine

    private val groupSet: MutableSet<String> = Collections.synchronizedSet(HashSet())

    @Volatile
    private var sendQueue: BlockingQueue<Typable>? = null


    override fun makeAction(fromKafka: BlockingQueue<Typable>, toKafka: BlockingQueue<Typable>) {
        sendQueue = toKafka
        fromKafka.poll(timeout_millis, time_out)
        println("Groups: $groupSet")
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
                    call.respondText("Test page, use commands /list;/add?add=name;/clear;/stop", ContentType.Text.Html)
                }

                get("/list") {
                    call.respondText("Groups list: $groupSet", ContentType.Text.Html)
                }

                get("/add") {
                    val groupName = this.context.request.queryParameters.get("add")
                    if (groupName == null) {
                        call.respondText("Groups list: $groupSet", ContentType.Text.Html)
                        return@get
                    }
                    groupSet.add(groupName)
                    call.respondText("Groups list: $groupSet", ContentType.Text.Html)
                }

                get("/clear") {
                    groupSet.clear()
                    call.respondText("Groups list: $groupSet", ContentType.Text.Html)
                }

                get("/stop") {
                    if (sendQueue == null) {
                        call.respondText("Couldn't cluster, not initialized yet", ContentType.Text.Html)
                        return@get
                    }
                    sendQueue!!.add(Exit())
                    call.respondText("Stopped", ContentType.Text.Html)
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