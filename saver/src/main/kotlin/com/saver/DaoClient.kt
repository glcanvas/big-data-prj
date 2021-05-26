package com.saver

import com.mongodb.MongoClient
import com.saver.dao.CommentMetaData
import com.saver.dao.Post
import com.saver.dao.PostMetaData
import dev.morphia.Datastore
import dev.morphia.Key
import dev.morphia.Morphia
import java.time.Instant

class DaoClient(host: String, private val port: Int) : AutoCloseable {
    private val client: MongoClient = MongoClient(host, port)
    private val datastore: Datastore

    init {
        val morphia = Morphia()
        morphia.mapPackage("com.saver.dao")
        datastore = morphia.createDatastore(client, "vk")
        datastore.ensureIndexes()
    }

    fun <T> put(t: T): Key<T> {
        return datastore.save(t)
    }

    fun <T> put(t: List<T>): Iterable<Key<T>> {
        return datastore.save(t)
    }

    fun <T> updateMetaPost(key: Int, updatedLastTime: Instant) {
        val query = datastore.createQuery(PostMetaData::class.java)
                .field("wallId")
                .equal(key);
        val update = datastore.createUpdateOperations(PostMetaData::class.java)
                .set("lastTime", updatedLastTime);
        datastore.update(query, update);
    }

    fun <T> updateMetaComment(wallId: Int, postId: Int, updatedLastTime: Instant) {
        val query = datastore.createQuery(CommentMetaData::class.java)
                .field("wallId")
                .equal(wallId)
                .field("postId")
                .equal(postId);
        val update = datastore.createUpdateOperations(CommentMetaData::class.java)
                .set("lastTime", updatedLastTime);
        datastore.update(query, update);
    }

    fun getPost(wallId: Int): List<Post> {
        return datastore.createQuery(Post::class.java)
                .field("wallId")
                .equal(wallId)
                .find()
                .toList()
    }

    override fun close() {
        client.close()
    }
}