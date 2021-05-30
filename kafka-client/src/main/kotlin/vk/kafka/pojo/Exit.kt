package vk.kafka.pojo

import vk.kafka.utils.Typable

class Exit: Typable {
    override fun type(): Typable.Type = Typable.Type.EXIT
}