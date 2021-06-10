import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.apache.log4j.Logger

class LogTest {
    private val logger: Logger = Logger.getLogger(LogTest::class.java)

    @Test
    fun logTest() {
        assertEquals(42, Integer.sum(19, 23))
        println("ANY")
    }
}