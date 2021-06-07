package vk.loader.dao

import java.time.Instant

interface Datable {
    fun getDate(): Instant
}