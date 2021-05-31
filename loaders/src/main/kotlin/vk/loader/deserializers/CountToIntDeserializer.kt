package vk.loader.deserializers

class CountToIntDeserializer : AbstractFieldExtractor() {
    override fun field(): String = "count"
}