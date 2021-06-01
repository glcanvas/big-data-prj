import com.mongodb.MongoClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.Network
import org.testcontainers.lifecycle.Startables
import vk.kafka.Publisher
import vk.kafka.pojo.ObjectHolder
import vk.kafka.pojo.RequestPost
import vk.loader.LoadLauncher
import vk.saver.SaverLauncher
import vk.saver.dao.PostMetaData
import java.time.Instant
import java.util.concurrent.TimeUnit
import java.util.stream.Stream

class IntegrationTest {
    val key = "8b34623d8b34623d8b34623d128b595d3788b348b34623deb51e7e4149c9e0837beb45a"

    private val network: Network = Network.newNetwork()

    private val kafkaContainer = KafkaContainer()
            .withNetwork(network)
    private val mongoDb = EmbeddedMongoDb()

    @BeforeEach
    fun startContainers() {
        var res = Startables.deepStart(Stream.of(kafkaContainer)).join()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun myTest() {
        val a = kafkaContainer.bootstrapServers
        val loader = LoadLauncher(key, a)
        val saver = SaverLauncher(a, "localhost", mongoDb.getPort())

        val loaderFuture = loader.run()
        val saverFuture = saver.run()


        val publisher = Publisher(a, "t1")
        publisher.publish(ObjectHolder(RequestPost(-86529522, Instant.MIN)))

        val client = MongoClient("localhost", mongoDb.getPort())
        client.getDatabase("FFF")
        Thread.sleep(TimeUnit.SECONDS.toMillis(10))
        loader.close()
        saver.close()
        loaderFuture.get()
        saverFuture.get()
    }
}