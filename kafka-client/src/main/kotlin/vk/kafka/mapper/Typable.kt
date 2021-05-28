package vk.kafka.mapper

import java.lang.IllegalArgumentException

interface Typable {
    fun type(): Type

    enum class Type(public val messageType: String) {
        RESPONSE_POST("ResponsePost"),
        RESPONSE_COMMENT("ResponseComment"),
        REQUEST_POST("RequestPost"),
        REQUEST_COMMENT("RequestComment"),
        EXIT("Exit");

        companion object {
            fun resolve(type: String): Type {
                for (i in values()) {
                    if (i.messageType == type) {
                        return i
                    }
                }
                throw IllegalArgumentException("unexpected type: $type")
            }
        }
    }
}