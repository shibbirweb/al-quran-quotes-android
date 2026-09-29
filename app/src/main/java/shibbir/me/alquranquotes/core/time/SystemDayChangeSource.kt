package shibbir.me.alquranquotes.core.time

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

/**
 * Listens for the system's date changed, time changed, and time zone changed broadcasts while
 * [dayChanges] is collected, and unregisters its receiver when collection stops.
 *
 * No exported flag is needed because all three actions are protected system broadcasts, which
 * no other app can send.
 */
class SystemDayChangeSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : DayChangeSource {

    override fun dayChanges(): Flow<Unit> = callbackFlow {
        val dayChangeIntentFilter = IntentFilter(Intent.ACTION_DATE_CHANGED)
        dayChangeIntentFilter.addAction(Intent.ACTION_TIME_CHANGED)
        dayChangeIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED)
        val dayChangeReceiver = createDayChangeReceiver { trySend(Unit) }
        context.registerReceiver(dayChangeReceiver, dayChangeIntentFilter)
        awaitClose { context.unregisterReceiver(dayChangeReceiver) }
    }

    private fun createDayChangeReceiver(onDayChange: () -> Unit): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(receiverContext: Context, dayChangeIntent: Intent) {
                onDayChange()
            }
        }
    }
}
