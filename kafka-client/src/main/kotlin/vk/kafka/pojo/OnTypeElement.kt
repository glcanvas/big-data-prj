package vk.kafka.pojo

interface OnTypeElement {

    fun onElement(item: Typable) {
        when(item.type()) {
            // Typable.Type.EXIT
        }
    }
}