package shibbir.me.alquranquotes.testing

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.Build

/**
 * Context that records the receivers registered and unregistered through it. It still passes
 * every call on to [baseContext], so the platform really registers the receiver: a registration
 * the platform rejects, or unregistering a receiver that was never registered, still throws.
 * Both the two-argument and the flags overload of registerReceiver are recorded, so the tests
 * stay meaningful if SystemDayChangeSource switches between them.
 */
class ReceiverRecordingContext(baseContext: Context) : ContextWrapper(baseContext) {

    val registeredReceivers = mutableListOf<BroadcastReceiver>()
    val registeredIntentFilters = mutableListOf<IntentFilter>()
    val unregisteredReceivers = mutableListOf<BroadcastReceiver>()

    /**
     * Forwards the caller's registration unchanged. Lint cannot see the caller's filter here, but
     * the only caller, SystemDayChangeSource, registers protected system broadcasts only.
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter?): Intent? {
        val stickyIntent = super.registerReceiver(receiver, filter)
        recordRegistration(receiver, filter)
        return stickyIntent
    }

    /**
     * Forwards the caller's registration and [flags] unchanged. Lint cannot see the caller's
     * filter or flags here, but the only caller, SystemDayChangeSource, registers protected
     * system broadcasts only. The platform added this overload in API 26 (Android 8.0), so it is
     * only ever called there.
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @TargetApi(Build.VERSION_CODES.O)
    override fun registerReceiver(
        receiver: BroadcastReceiver?,
        filter: IntentFilter?,
        flags: Int,
    ): Intent? {
        val stickyIntent = super.registerReceiver(receiver, filter, flags)
        recordRegistration(receiver, filter)
        return stickyIntent
    }

    override fun unregisterReceiver(receiver: BroadcastReceiver?) {
        super.unregisterReceiver(receiver)
        unregisteredReceivers += requireNotNull(receiver)
    }

    private fun recordRegistration(receiver: BroadcastReceiver?, filter: IntentFilter?) {
        registeredReceivers += requireNotNull(receiver)
        registeredIntentFilters += requireNotNull(filter)
    }
}
