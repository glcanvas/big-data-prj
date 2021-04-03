package data

import java.time.Instant

data class VkProperties(
        val publicKey: String,
        val groupName: String,
        val dateFrom: Instant,
        val mongoDbHostPort: String,
        val iterationsCount: Int) {
    companion object {

    }
}
