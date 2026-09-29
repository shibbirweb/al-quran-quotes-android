package shibbir.me.alquranquotes.testing

import shibbir.me.alquranquotes.data.local.AyahDao
import shibbir.me.alquranquotes.data.local.AyahEntity

class FakeAyahDao : AyahDao {

    private val ayahs = mutableListOf<AyahEntity>()

    override suspend fun count(): Int = ayahs.size

    override suspend fun getAt(position: Int): AyahEntity? =
        ayahs.sortedWith(compareBy({ it.surah }, { it.ayah })).getOrNull(position)

    override suspend fun insertAll(ayahs: List<AyahEntity>) {
        ayahs.forEach { ayah ->
            if (this.ayahs.none { it.surah == ayah.surah && it.ayah == ayah.ayah }) {
                this.ayahs += ayah
            }
        }
    }
}
