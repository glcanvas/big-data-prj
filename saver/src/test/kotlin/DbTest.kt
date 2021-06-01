import vk.saver.DaoClient
import vk.saver.dao.CommentMetaData
import vk.saver.dao.Post
import vk.saver.dao.PostMetaData
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
                CommentMetaData("1", 123, 1, Instant.parse("2020-01-01T01:01:01.000Z"))
        )
        Assertions.assertEquals("1", post.id)

        Assertions.assertThrows(RuntimeException::class.java) {
            client.put(
                    CommentMetaData("2", 123, 1, Instant.parse("2020-01-01T01:01:01.000Z")))
        }
        client.close()
    }

    @Test
    fun addGetTest() {
        val client = DaoClient("localhost", port)
        var p1 = client.put(Post(1, 1, Instant.parse("2020-01-01T01:01:01.000Z"), "", 1, 1,1, Collections.emptyList()))
        var p2 = client.put(Post(2, 1, Instant.parse("2020-01-01T01:01:01.000Z"), "", 1, 1,1, Collections.emptyList()))
        val posts = client.getPost(1)
        Assertions.assertEquals(2, posts.size)
        client.close()
    }

    @Test
    fun addUpdateTest() {
        val client = DaoClient("localhost", port)
        client.put(PostMetaData(1, "q", Instant.parse("2020-01-01T01:01:01.000Z")))
        client.updateMetaPost(1, Instant.parse("2021-01-01T01:01:01.000Z"))
        val meta = client.getPostMeta(1)[0]
        Assertions.assertEquals(Instant.parse("2021-01-01T01:01:01.000Z"), meta.lastTime)
        client.close()
    }

    @AfterEach
    fun tearDown() {
        mongoDb.close()
    }

}