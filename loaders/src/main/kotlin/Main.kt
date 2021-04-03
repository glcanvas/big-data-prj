import data.VkProperties
import vk.VkDataFetcher
import java.time.Instant



fun main() {
    val properties = VkProperties("8b34623d8b34623d8b34623d128b595d3788b348b34623deb51e7e4149c9e0837beb45a",
            "sabatoncjlub",
            Instant.parse("2017-05-05T20:12:00.00Z"),
            "mongodb://localhost",
            2)

    val fetcher = VkDataFetcher(properties)

    fetcher.safeFetchLocally(1)
}
