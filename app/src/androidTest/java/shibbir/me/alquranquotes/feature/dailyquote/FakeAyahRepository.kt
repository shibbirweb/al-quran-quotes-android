package shibbir.me.alquranquotes.feature.dailyquote

import shibbir.me.alquranquotes.data.repository.AyahRepository
import shibbir.me.alquranquotes.model.Ayah
import java.util.concurrent.ConcurrentHashMap

/**
 * Device-test repository that answers at once with the ayah stored for the requested day, or
 * null (shown as Error) when that day has none. Thread safe, because tests fill it from the
 * instrumentation thread while the ViewModel reads it on the main thread.
 */
class FakeAyahRepository : AyahRepository {

    val dailyAyahsByEpochDay = ConcurrentHashMap<Long, Ayah>()

    override suspend fun getDailyAyah(epochDay: Long): Ayah? = dailyAyahsByEpochDay[epochDay]
}
