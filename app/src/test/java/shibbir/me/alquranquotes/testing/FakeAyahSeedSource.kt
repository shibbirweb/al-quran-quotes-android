package shibbir.me.alquranquotes.testing

import shibbir.me.alquranquotes.data.local.AyahEntity
import shibbir.me.alquranquotes.data.seed.AyahSeedSource

class FakeAyahSeedSource(private val ayahs: List<AyahEntity>) : AyahSeedSource {

    var loadCount = 0
        private set

    override suspend fun load(): List<AyahEntity> {
        loadCount++
        return ayahs
    }
}
