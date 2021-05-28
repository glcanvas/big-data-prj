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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.Network
import org.testcontainers.lifecycle.Startables
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap
import java.time.Duration
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
        println(res)

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