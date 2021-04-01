import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import java.io.Closeable
import com.mongodb.MongoClient
import org.bson.Document


class EmbeddedMongoDb : Closeable {

    private val mongoDbExecutable: MongodExecutable
    private val port: Int

    init {
        val starter: MongodStarter = MongodStarter.getDefaultInstance()

        port = Network.getFreeServerPort()
        val mongoConfig = MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(Net(port, Network.localhostIsIPv6()))
                .build()

        mongoDbExecutable = starter.prepare(mongoConfig)
        mongoDbExecutable.start()
    }

    fun getPort(): Int {
        return port
    }

    override fun close() {
        mongoDbExecutable.stop()
    }

}


fun main() {

    val a = EmbeddedMongoDb()

    var mongoClient = MongoClient("localhost", a.getPort())
    mongoClient.use {
        val db = it.getDatabase("db")
        db.createCollection("lastIdx")
        db.getCollection("lastIdx")
                .insertOne(Document().append("name", "sabaton").append("date", "2020"))
    }

    mongoClient = MongoClient("localhost", a.getPort())
    mongoClient.use {
        val db = it.getDatabase("db")
        for(v in db.getCollection("lastIdx").find()) {
            println(v)
        }
    }



}