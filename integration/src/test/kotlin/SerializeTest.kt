import org.junit.jupiter.api.Test
import vk.kafka.pojo.ObjectHolder
import vk.kafka.pojo.Post
import vk.kafka.utils.ObjectHolderProcessor
import java.time.Instant
import java.util.*

class SerializeTest {
    @Test
    fun simpleTest() {
        val data = ObjectHolder(Post(0, 0, Instant.MIN, "", 228, 0, 0, Collections.emptyList()))
        val raw = ObjectHolderProcessor().serialize("top", data)
        val dataDeser = ObjectHolderProcessor().deserialize("top", raw)
        println(dataDeser)
        println(data)
    }
}