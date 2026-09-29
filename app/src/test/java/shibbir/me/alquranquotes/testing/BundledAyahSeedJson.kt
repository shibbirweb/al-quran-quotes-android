package shibbir.me.alquranquotes.testing

import shibbir.me.alquranquotes.data.seed.AYAHS_ASSET_NAME
import shibbir.me.alquranquotes.data.seed.AyahSeed

/**
 * Reads the bundled `ayahs.json` from the unit-test classpath, where `src/main/assets` is a
 * resources directory. The app classes and the test resources share one class loader, so the
 * loader of [AyahSeed] finds the asset.
 */
fun readBundledAyahSeedJson(): String {
    val classLoader = requireNotNull(AyahSeed::class.java.classLoader)
    val assetStreamOrNull = classLoader.getResourceAsStream(AYAHS_ASSET_NAME)
    val assetStream = requireNotNull(assetStreamOrNull) {
        "$AYAHS_ASSET_NAME is not on the unit-test classpath"
    }
    val assetReader = assetStream.bufferedReader()
    return assetReader.use { reader -> reader.readText() }
}
