package shibbir.me.alquranquotes.navigation

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * A kotlinx.serialization decoder that hands out [queuedValues] in order, so a test can decode a
 * route without a JSON library. With [decodesSequentially] false it reports every structure as
 * already finished, which is how a test decodes a route with a missing property.
 */
@OptIn(ExperimentalSerializationApi::class)
class QueuedValueDecoder(
    queuedValues: List<Any>,
    private val decodesSequentially: Boolean = true,
) : AbstractDecoder() {

    private val remainingValues = ArrayDeque(queuedValues)

    override val serializersModule: SerializersModule = EmptySerializersModule()

    override fun decodeSequentially(): Boolean = decodesSequentially

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        return CompositeDecoder.DECODE_DONE
    }

    override fun decodeValue(): Any = remainingValues.removeFirst()
}
