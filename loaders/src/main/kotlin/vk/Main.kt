package vk

import data.VkProperties
import org.apache.log4j.Logger
import org.litote.kmongo.KMongo
import se.softhouse.jargo.Argument
import se.softhouse.jargo.Arguments.fileArgument
import se.softhouse.jargo.Arguments.helpArgument
import se.softhouse.jargo.CommandLineParser
import java.io.File
import java.time.Instant
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.collections.ArrayList

private const val PUBLIC_KEY = "public_key"
private const val DB_PATH = "db_path"
private const val GROUP_NAME = "group_name"
private const val TIME_FROM = "time_from"
private const val ITERATIONS = "iterations"


class Main {
    private val helpArgument: Argument<*> = helpArgument("-h")
    private val baseProperty: Argument<File> = fileArgument("--base").required().build()
    private val vkConfigProperties: Argument<List<File>> = fileArgument("--vk").repeated().required().build()

    private fun readPropertiesFile(file: File): Properties {
        val properties = Properties()
        file.inputStream().use {
            properties.load(it);
        }
        return properties
    }

    private val logger: Logger = Logger.getLogger(Main::class.java)
    fun main(args: Array<String>) {

        val arguments = CommandLineParser.withArguments(baseProperty, vkConfigProperties)
                .andArguments(helpArgument)
                .parse(args.toList())

        val baseProperties = readPropertiesFile(arguments.get(baseProperty)!!);
        val vkKey = baseProperties.getProperty(PUBLIC_KEY)
        val dbPath = baseProperties.getProperty(DB_PATH)

        val groupConfigs: List<VkProperties> = arguments.get(vkConfigProperties)!!.map { readPropertiesFile(it) }
                .map {
                    val groupName = it.getProperty(GROUP_NAME)
                    val timeFrom = Instant.parse(it.getProperty(TIME_FROM))
                    val iterations = Integer.parseInt(it.getProperty(ITERATIONS))
                    VkProperties(vkKey, groupName, timeFrom, dbPath, iterations)
                }

        val mongoClient = KMongo.createClient(dbPath)
        logger.info("configs: $groupConfigs")
        val executorService: ExecutorService = Executors.newFixedThreadPool(5);
        mongoClient.use {
            val tasks: MutableList<Future<*>> = ArrayList()
            for (config in groupConfigs) {
                val vkDataFetcher = VkDataFetcher(config)
                val vkDataStorage = VkDataStorage(mongoClient, config, vkDataFetcher)
                tasks.add(executorService.submit {
                    vkDataStorage.processData()
                })
            }
            tasks.forEach {
                it.get()
            }
        }

    }
}

fun main(args: Array<String>) {
    Main().main(args)
}