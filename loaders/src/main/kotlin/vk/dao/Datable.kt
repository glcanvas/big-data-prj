package vk.dao

import java.time.Instant

interface Datable {
    fun getDate(): Instant
}