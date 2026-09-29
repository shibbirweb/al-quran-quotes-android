package shibbir.me.alquranquotes.testing

import shibbir.me.alquranquotes.data.repository.AyahRepository
import shibbir.me.alquranquotes.model.Ayah

class FakeAyahRepository : AyahRepository {

    var dailyAyah: Ayah? = null
    var error: Exception? = null
    val requestedEpochDays = mutableListOf<Long>()

    override suspend fun getDailyAyah(epochDay: Long): Ayah? {
        requestedEpochDays += epochDay
        error?.let { throw it }
        return dailyAyah
    }
}
