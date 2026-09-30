package shibbir.me.alquranquotes.navigation

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import org.junit.Assert.assertEquals
import org.junit.Test

/** Checks the route contract that Navigation Compose and the quote editor rely on. */
@OptIn(ExperimentalSerializationApi::class)
class AppDestinationTest {

    @Test
    fun editQuoteHoldsItsQuoteId() {
        val editQuote = AppDestination.EditQuote(quoteId = 7L)

        assertEquals(7L, editQuote.quoteId)
    }

    @Test
    fun quoteIdKeyMatchesThePropertyName() {
        val propertyName = AppDestination.EditQuote::quoteId.name

        assertEquals(propertyName, AppDestination.EditQuote.QUOTE_ID_KEY)
    }

    @Test
    fun dataObjectDestinationsAreDistinct() {
        val dataObjectDestinations = listOf(
            AppDestination.Home,
            AppDestination.Quotes,
            AppDestination.Settings,
            AppDestination.AddQuote,
        )

        val distinctDestinations = dataObjectDestinations.toSet()

        assertEquals(dataObjectDestinations.size, distinctDestinations.size)
    }

    @Test
    fun editQuoteRouteEncodesItsQuoteId() {
        val recordingEncoder = RecordingEncoder()

        recordingEncoder.encodeSerializableValue(
            AppDestination.serializer(),
            AppDestination.EditQuote(quoteId = 7L),
        )

        val encodedQuoteId = recordingEncoder.encodedValues.last()
        assertEquals(7L, encodedQuoteId)
    }

    @Test
    fun editQuoteRouteDecodesItsQuoteId() {
        val editQuoteSerialName = AppDestination.EditQuote.serializer().descriptor.serialName
        val queuedValueDecoder = QueuedValueDecoder(listOf(editQuoteSerialName, 7L))

        val decodedDestination = queuedValueDecoder.decodeSerializableValue(
            AppDestination.serializer(),
        )

        assertEquals(AppDestination.EditQuote(quoteId = 7L), decodedDestination)
    }

    @Test(expected = MissingFieldException::class)
    fun editQuoteRouteWithoutQuoteIdFailsToDecode() {
        val emptyDecoder = QueuedValueDecoder(
            queuedValues = emptyList(),
            decodesSequentially = false,
        )

        emptyDecoder.decodeSerializableValue(AppDestination.EditQuote.serializer())
    }
}
