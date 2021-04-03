package vk

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import data.MetaCollection
import data.PostBytes
import data.VkProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.log4j.Logger
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection
import org.litote.kmongo.updateOne
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class VkDataStorage(dbClient: MongoClient,
                    private val vkProperties: VkProperties,
                    private val vkDataFetcher: VkDataFetcher) {
    private val logger: Logger = Logger.getLogger(VkDataStorage::class.java)

    private val database: MongoDatabase = dbClient.getDatabase(DATABASE_NAME)
    private val postCollection: MongoCollection<PostBytes>
    private val metaCollection: MongoCollection<MetaCollection>

    private var lastMetaData: MetaCollection
    private val groupName = vkProperties.groupName

    companion object {
        private const val DATABASE_NAME = "vk"
        private const val POSTS_COLLECTION = "posts"
        private const val META_COLLECTION = "meta"
    }

    init {
        val names = database.listCollectionNames().toList()

        if (!names.contains(POSTS_COLLECTION)) {
            database.createCollection(POSTS_COLLECTION)
            logger.info("create collection $POSTS_COLLECTION")
        }
        if (!names.contains(META_COLLECTION)) {
            database.createCollection(META_COLLECTION)
            logger.info("create collection $META_COLLECTION")
        }
        postCollection = database.getCollection<PostBytes>(POSTS_COLLECTION)
        metaCollection = database.getCollection<MetaCollection>(META_COLLECTION)
        lastMetaData = loadMeta()
        logger.info("$groupName loaded Meta record: $lastMetaData")
    }

    fun loadMeta(): MetaCollection {
        metaCollection.find(MetaCollection::groupName eq vkProperties.groupName).first()
                ?: metaCollection.insertOne(MetaCollection(vkProperties.groupName, vkProperties.dateFrom))
        return metaCollection.find(MetaCollection::groupName eq vkProperties.groupName).first()!!
    }

    fun updateMeta(time: Instant) {
        metaCollection.updateOne(MetaCollection::groupName eq vkProperties.groupName,
                MetaCollection(vkProperties.groupName, time))
    }

    fun processData() {
        logger.info("$groupName begin process data with iterations: ${vkProperties.iterationsCount}")
        for (i in 1..vkProperties.iterationsCount) {
            logger.info("$groupName begin iteration: $i/${vkProperties.iterationsCount}")
            val (lastTime, rawData) = vkDataFetcher.safeFetchLocally(1)
            runBlocking {
                val result = Collections.synchronizedList(ArrayList<PostBytes>())
                rawData.map { launch { result.add(PostBytes.toPost(it)) } }.joinAll()
                logger.info("$groupName images loaded")

                val insertPostJob = launch(Dispatchers.IO) {
                    postCollection.insertMany(result)
                    logger.info("$groupName wrote posts: ${result.size}")
                }
                val updateMetaJob = launch(Dispatchers.IO) {
                    updateMeta(lastTime)
                    logger.info("$groupName meta data updated new time: $lastTime")
                }
                insertPostJob.join()
                updateMetaJob.join()
            }
            logger.info("$groupName end iteration: $i/${vkProperties.iterationsCount}")
        }
        logger.info("$groupName end")
    }


}
