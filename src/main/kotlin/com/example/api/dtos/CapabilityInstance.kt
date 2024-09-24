package com.example.api.dtos

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = CapabilityInstanceSerializer::class)
sealed interface CapabilityInstance {
    val value: String

    @Serializable(with = CapabilityInstanceSerializer::class)
    enum class RangeCapabilityInstance(override val value: String): CapabilityInstance {
        TEMPERATURE("temperature")
    }

    @Serializable(with = CapabilityInstanceSerializer::class)
    enum class OnOffCapabilityInstance(override val value: String): CapabilityInstance {
        ON_OFF("on")
    }
}

object CapabilityInstanceSerializer : KSerializer<CapabilityInstance> {
    override fun serialize(encoder: Encoder, value: CapabilityInstance) {
        encoder.encodeString(value.value)
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(CapabilityInstance.Companion::class.java.name, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): CapabilityInstance {
        // Реализация десериализации, если требуется
        val value = decoder.decodeString()
        return when (value) {
            CapabilityInstance.RangeCapabilityInstance.TEMPERATURE.value -> CapabilityInstance.RangeCapabilityInstance.TEMPERATURE
            CapabilityInstance.OnOffCapabilityInstance.ON_OFF.value -> CapabilityInstance.OnOffCapabilityInstance.ON_OFF
            else -> throw IllegalArgumentException("Unknown value: $value")
        }
    }
}