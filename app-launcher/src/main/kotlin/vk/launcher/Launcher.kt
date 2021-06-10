package vk.launcher

import vk.launcher.Launcher.Companion.load

const val KEY = "VK_KEY"
const val KAFKA_ADDRESS = "KAFKA_ADDRESS"
const val HTTP_PORT = "HTTP_PORT"
const val MONGO_HOST = "MONGO_HOST"
const val MONGO_PORT = "MONGO_PORT"
const val IMAGE_DIR = "IMAGE_DIR"


class Launcher private constructor(val envMap: Map<String, String>) {

    companion object {

        fun load(): Launcher {
            val envs = System.getenv()
            println("Envs: $envs")
            return Launcher(envs)
        }
    }

    fun getVkKey(): String {
        return envMap[KEY]!!
    }

    fun getKafkaAddress(): String {
        return envMap[KAFKA_ADDRESS]!!
    }

    fun getHttpPort(): Int {
        return Integer.parseInt(envMap[HTTP_PORT]!!)
    }

    fun getMongoHost(): String {
        return envMap[MONGO_HOST]!!
    }

    fun getMongoPort(): Int {
        return Integer.parseInt(envMap[MONGO_PORT]!!)
    }

    fun getImageDir(): String {
        return envMap[IMAGE_DIR]!!
    }
}

fun main() {
    load()
    println(System.getenv()["PATH"])
}