import com.mongodb.MongoClient
import com.saver.DaoClient
import com.saver.dao.CommentMetaData
import com.saver.dao.Post
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.lang.RuntimeException
import java.time.Instant
import java.util.*

class DbTest {
    private lateinit var mongoDb: EmbeddedMongoDb
    private var port: Int = 0

    @BeforeEach
    fun init() {
        mongoDb = EmbeddedMongoDb()
        port = mongoDb.getPort()
    }

    @Test
    fun simpleTest() {
        val client = DaoClient("localhost", port)
        val post = client.put(
                Post(123, 1, Instant.parse("2020-01-01T01:01:01.000Z"), "!", 0, 0, 0, Collections.emptyList())
        )
        Assertions.assertEquals(
                123, post.id
        )
        client.close()
    }

    @Test
    fun simpleTest2() {
        val client = DaoClient("localhost", port)
        val post = client.put(
                CommentMetaData(1, 123, 1, Instant.parse("2020-01-01T01:01:01.000Z"))
        )
        Assertions.assertEquals(1, post.id)

        Assertions.assertThrows(RuntimeException::class.java) {
            client.put(
                    CommentMetaData(2, 123, 1, Instant.parse("2020-01-01T01:01:01.000Z")))
        }
        client.close()
    }

    @AfterEach
    fun tearDown() {
        mongoDb.close()
    }

}