package vk.deserializers

class CountToIntDeserializer : AbstractFieldExtractor() {
    override fun field(): String = "count"
}