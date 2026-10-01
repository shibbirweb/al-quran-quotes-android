package shibbir.me.alquranquotes.navigation

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * A kotlinx.serialization encoder that records every primitive value in the order it was
 * written, so a test can check what a route serializer writes without a JSON library.
 */
@OptIn(ExperimentalSerializationApi::class)
class RecordingEncoder : AbstractEncoder() {

    private val recordedValues = mutableListOf<Any>()

    /** Every value written so far, oldest first. */
    val encodedValues: List<Any>
        get() = recordedValues.toList()

    override val serializersModule: SerializersModule = EmptySerializersModule()

    override fun encodeValue(value: Any) {
        recordedValues.add(value)
    }
}
