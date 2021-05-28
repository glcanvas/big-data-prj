import info.batey.kafka.unit.KafkaUnit
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.LongSerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*


class KafkaTest {
    lateinit var kafkaUnitServer: KafkaUnit

    @BeforeEach
    fun init() {

    }

    @AfterEach
    fun tearDown() {
        kafkaUnitServer.shutdown()
    }

    @Test
    fun simpleTest() {
        kafkaUnitServer = KafkaUnit(50001, 50002)
        kafkaUnitServer.startup()
        val props = Properties()
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer::class.java.getCanonicalName())
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.getCanonicalName())
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUnitServer.kafkaConnect)

        val producer: Producer<Long, String> = KafkaProducer(props)

        producer.send(ProducerRecord("!!", 123L, "hello"))
    }
}