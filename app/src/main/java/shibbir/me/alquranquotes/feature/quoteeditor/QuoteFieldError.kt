package shibbir.me.alquranquotes.feature.quoteeditor

import androidx.annotation.StringRes
import shibbir.me.alquranquotes.R

/** Why a quote editor field is not valid, with the message shown under the field. */
enum class QuoteFieldError(@param:StringRes val messageResId: Int) {
    REQUIRED(R.string.quote_editor_error_required),
    NOT_A_NUMBER(R.string.quote_editor_error_not_a_number),
    SURAH_NUMBER_OUT_OF_RANGE(R.string.quote_editor_error_surah_out_of_range),
    AYAH_NUMBER_TOO_SMALL(R.string.quote_editor_error_ayah_number_too_small),
}
