package shibbir.me.alquranquotes.core.time

import android.content.Context
import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.testing.ReceiverRecordingContext

/**
 * The date, time, and time zone broadcasts are protected: only the system can send them, so a
 * test (which runs as the app) gets a SecurityException when it tries, and changing the device
 * clock would make CI flaky. Instead the receiver is registered for real through
 * [ReceiverRecordingContext], so the platform still checks the registration and the
 * unregistration, and the tests hand the broadcast intents straight to that receiver.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class SystemDayChangeSourceTest {

    private val targetContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val receiverRecordingContext = ReceiverRecordingContext(targetContext)
    private val dayChangeSource = SystemDayChangeSource(receiverRecordingContext)
    private val receivedDayChanges = mutableListOf<Unit>()

    private val dayChangeActions = setOf(
        Intent.ACTION_DATE_CHANGED,
        Intent.ACTION_TIME_CHANGED,
        Intent.ACTION_TIMEZONE_CHANGED,
    )

    @Test
    fun collectingRegistersOneReceiverForDateTimeAndTimeZoneChanges() = runTest {
        val dayChangeCollection = startCollectingDayChanges()

        val dayChangeIntentFilter = receiverRecordingContext.registeredIntentFilters.single()
        val listenedActionsIterator = dayChangeIntentFilter.actionsIterator()
        val listenedActions = listenedActionsIterator.asSequence().toSet()
        dayChangeCollection.cancelAndJoin()

        assertEquals(1, receiverRecordingContext.registeredReceivers.size)
        assertEquals(dayChangeActions, listenedActions)
    }

    @Test
    fun emitsOnceForEveryDayChangeBroadcast() = runTest {
        val dayChangeCollection = startCollectingDayChanges()
        val dayChangeReceiver = receiverRecordingContext.registeredReceivers.single()

        dayChangeActions.forEach { dayChangeAction ->
            val dayChangeIntent = Intent(dayChangeAction)
            dayChangeReceiver.onReceive(targetContext, dayChangeIntent)
        }
        runCurrent()
        dayChangeCollection.cancelAndJoin()

        assertEquals(dayChangeActions.size, receivedDayChanges.size)
    }

    @Test
    fun emitsNothingBeforeAnyBroadcast() = runTest {
        val dayChangeCollection = startCollectingDayChanges()

        dayChangeCollection.cancelAndJoin()

        assertTrue(receivedDayChanges.isEmpty())
    }

    @Test
    fun stoppingCollectionUnregistersTheSameReceiver() = runTest {
        val dayChangeCollection = startCollectingDayChanges()
        val dayChangeReceiver = receiverRecordingContext.registeredReceivers.single()

        dayChangeCollection.cancelAndJoin()

        assertEquals(listOf(dayChangeReceiver), receiverRecordingContext.unregisteredReceivers)
    }

    /**
     * Collects on an unconfined dispatcher and runs pending work, so the receiver is registered
     * when this returns and each broadcast is collected as soon as it is received.
     */
    private fun TestScope.startCollectingDayChanges(): Job {
        val collectingDispatcher = UnconfinedTestDispatcher(testScheduler)
        val dayChangeCollection = launch(collectingDispatcher) {
            val dayChanges = dayChangeSource.dayChanges()
            dayChanges.toList(receivedDayChanges)
        }
        runCurrent()
        return dayChangeCollection
    }
}
