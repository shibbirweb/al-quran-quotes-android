package shibbir.me.alquranquotes.core.time

/** Supplies the current local day as days since 1970-01-01. */
fun interface EpochDayProvider {
    fun today(): Long
}
