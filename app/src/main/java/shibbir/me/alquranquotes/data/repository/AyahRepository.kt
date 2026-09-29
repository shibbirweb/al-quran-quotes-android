package shibbir.me.alquranquotes.data.repository

import shibbir.me.alquranquotes.model.Ayah

interface AyahRepository {
    /** Returns the ayah for [epochDay], or null when no ayahs are available. */
    suspend fun getDailyAyah(epochDay: Long): Ayah?
}
