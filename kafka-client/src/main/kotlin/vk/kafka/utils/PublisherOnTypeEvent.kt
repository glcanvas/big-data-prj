package vk.kafka.utils

import vk.kafka.Publisher

abstract class PublisherOnTypeEvent(protected val publisher: Publisher) : DefaultOnTypeElement {
}