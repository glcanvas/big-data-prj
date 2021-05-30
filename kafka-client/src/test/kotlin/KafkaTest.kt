import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.LongSerializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.Network
import org.testcontainers.lifecycle.Startables
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap
import vk.kafka.Consumer
import vk.kafka.Publisher
import vk.kafka.pojo.Comment
import vk.kafka.pojo.ObjectHolder
import vk.kafka.pojo.Post
import vk.kafka.utils.DefaultOnTypeElement
import vk.kafka.utils.OnTypeElement
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.stream.Stream


class KafkaTest {


    private val network: Network = Network.newNetwork()

    private val kafkaContainer = KafkaContainer()
            .withNetwork(network)

    @BeforeEach
    fun startContainers() {
        var res = Startables.deepStart(Stream.of(kafkaContainer)).join()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun simpleTest() {
        val a = kafkaContainer.getBootstrapServers()
        println(a)

        val producer = getProducer(kafkaContainer)
        val consumer = getConsumer(kafkaContainer)

        val r = producer.send(ProducerRecord("tc-0", "key", "hello")).get()

        consumer.subscribe(Collections.singleton("tc-0"))
        val res = consumer.poll(Duration.ofMinutes(5))

        // ==================
        val callable: OnTypeElement = object : DefaultOnTypeElement {
            override fun onPost(item: Post) {
                Assertions.assertEquals(228, item.likes)
            }

        }
        val publisher = Publisher(a, "project")
        val cons1 = Consumer(a, "project")
        publisher.publish(ObjectHolder(Post(0, 0, Instant.MIN, "", 228, 0, 0, Collections.emptyList())))
        val cons2 = Consumer(a, "project")

        Assertions.assertEquals(1, cons1.fetch(callable))
        Assertions.assertEquals(1, cons2.fetch(callable))
    }

    private fun getConsumer(
            kafkaContainer: KafkaContainer): KafkaConsumer<String, String> {
        return KafkaConsumer(
                ImmutableMap.of<String?, Any?>(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                        kafkaContainer.bootstrapServers,
                        ConsumerConfig.GROUP_ID_CONFIG,
                        "tc-0",
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                        "earliest"),
                StringDeserializer(),
                StringDeserializer())
    }

    private fun getProducer(kafkaContainer: KafkaContainer): KafkaProducer<String, String> {
        return KafkaProducer(ImmutableMap.of<String?, Any?>(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaContainer.bootstrapServers),
                StringSerializer(),
                StringSerializer())
    }

}