package vk.launcher

const val KEY = "vk.key"
const val KAFKA_ADDRESS = "kafka.address"
const val CONFIG_PATH = "config.path"
const val MONGO_HOST = "mongo.host"
const val MONGO_PORT = "mongo.port"


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

    fun getConfigPath(): String {
        return envMap[CONFIG_PATH]!!
    }

    fun getMongoHost(): String {
        return envMap[MONGO_HOST]!!
    }

    fun getMongoPort(): Int {
        return Integer.parseInt(envMap[MONGO_PORT]!!)
    }
}