package shibbir.me.alquranquotes.data.seed

import shibbir.me.alquranquotes.model.Ayah

/**
 * The bundled ayah set and its [version]. Bump the version in `assets/ayahs.json` whenever the
 * ayahs change so installed apps replace their stored copy.
 */
data class AyahSeed(
    val version: Int,
    val ayahs: List<Ayah>,
)
