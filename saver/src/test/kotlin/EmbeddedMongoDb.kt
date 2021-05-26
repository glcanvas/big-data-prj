import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import java.io.Closeable
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

