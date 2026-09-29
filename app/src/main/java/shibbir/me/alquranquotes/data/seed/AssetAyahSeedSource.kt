package shibbir.me.alquranquotes.data.seed

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import shibbir.me.alquranquotes.core.coroutines.IoDispatcher
import javax.inject.Inject

internal const val AYAHS_ASSET_NAME = "ayahs.json"

/**
 * Reads the ayahs bundled in `assets/ayahs.json`. The file read and the parsing run on
 * [ioDispatcher], so [load] is safe to call from any thread.
 */
class AssetAyahSeedSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : AyahSeedSource {

    override suspend fun load(): AyahSeed = withContext(ioDispatcher) {
        val ayahSeedJson = readAyahSeedJson()
        parseAyahSeedJson(ayahSeedJson)
    }

    private fun readAyahSeedJson(): String {
        val assetStream = context.assets.open(AYAHS_ASSET_NAME)
        val assetReader = assetStream.bufferedReader()
        return assetReader.use { reader -> reader.readText() }
    }
}
