package vk.cron

import vk.kafka.app.AbstractApp
import vk.kafka.pojo.RequestPost
import vk.kafka.utils.Typable
import vk.saver.DaoClient
import java.io.FileInputStream
import java.time.Instant
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit

class CronLauncher(bootstrap: String, private val path: String, host: String, port: Int) : AbstractApp(bootstrap) {
    private val daoClient = DaoClient(host, port)
    override fun makeAction(fromKafka: BlockingQueue<Typable>, toKafka: BlockingQueue<Typable>) {
        fromKafka.poll()
        val properties = readProperty()
        for (key in properties.stringPropertyNames()) {
            val value = properties.getProperty(key)
            if (key.startsWith("wall")) {
                val wallId = Integer.parseInt(value)
                val meta = daoClient.getPostMeta(wallId)
                val time:Instant = if (meta.isEmpty()) {
                    Instant.MIN
                } else {
                    meta[0].lastTime
                }
                toKafka.add(RequestPost(wallId, time))
            }
        }
        Thread.sleep(TimeUnit.SECONDS.toMillis(10))
    }

    override fun initialize() {
    }

    override fun appClose() {
        daoClient.close()
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