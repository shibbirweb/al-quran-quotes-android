package shibbir.me.alquranquotes.feature.dailyquote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import shibbir.me.alquranquotes.R
import shibbir.me.alquranquotes.model.Ayah

/** Language of the ayah's Arabic text, so TalkBack reads it with an Arabic voice. */
internal const val ARABIC_LANGUAGE_TAG = "ar"

/** Stateful entry point: connects [DailyQuoteScreen] to its [DailyQuoteViewModel]. */
@Composable
fun DailyQuoteRoute(
    modifier: Modifier = Modifier,
    viewModel: DailyQuoteViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Day change events can be missed or delayed while the app is in the background, so also
    // check the day on every resume.
    LifecycleResumeEffect(key1 = viewModel) {
        viewModel.refreshIfDayChanged()
        onPauseOrDispose {
            // Nothing to release: a load in progress keeps running in the ViewModel.
        }
    }
    DailyQuoteScreen(
        uiState = uiState,
        onRetry = viewModel::loadDailyQuote,
        modifier = modifier,
    )
}

/** Stateless daily quote screen: shows [uiState] and reports Retry taps through [onRetry]. */
@Composable
fun DailyQuoteScreen(
    uiState: DailyQuoteUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (uiState) {
            DailyQuoteUiState.Loading -> DailyQuoteLoading()
            DailyQuoteUiState.Error -> DailyQuoteError(onRetry = onRetry)
            is DailyQuoteUiState.Success -> DailyQuoteContent(ayah = uiState.ayah)
        }
    }
}

@Composable
private fun DailyQuoteLoading() {
    val loadingDescription = stringResource(R.string.daily_quote_loading)
    CircularProgressIndicator(
        modifier = Modifier.semantics { contentDescription = loadingDescription },
    )
}

@Composable
private fun DailyQuoteContent(ayah: Ayah) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        DailyQuoteTitle()
        AyahArabicText(arabicText = ayah.arabicText)
        AyahTranslation(translation = ayah.translation)
        AyahReference(ayah = ayah)
    }
}

@Composable
private fun DailyQuoteTitle() {
    Text(
        text = stringResource(R.string.daily_quote_title),
        modifier = Modifier.semantics { heading() },
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun AyahArabicText(arabicText: String) {
    val arabicAnnotatedText = rememberArabicAnnotatedText(arabicText)
    val headlineStyle = MaterialTheme.typography.headlineSmall
    val arabicTextStyle = headlineStyle.copy(
        textDirection = TextDirection.Rtl,
        // Relative, so the line height follows non-linear font scaling.
        lineHeight = 2.em,
    )
    Text(
        text = arabicAnnotatedText,
        style = arabicTextStyle,
        textAlign = TextAlign.Center,
    )
}

/**
 * Marks [arabicText] as Arabic. The locale is set on a span because span locales reach the
 * accessibility text, so TalkBack can read the ayah with an Arabic voice. A locale on the
 * TextStyle only affects drawing.
 */
@Composable
private fun rememberArabicAnnotatedText(arabicText: String): AnnotatedString {
    return remember(arabicText) {
        val arabicSpanStyle = SpanStyle(localeList = LocaleList(ARABIC_LANGUAGE_TAG))
        buildAnnotatedString {
            withStyle(arabicSpanStyle) {
                append(arabicText)
            }
        }
    }
}

@Composable
private fun AyahTranslation(translation: String) {
    Text(
        text = translation,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun AyahReference(ayah: Ayah) {
    Text(
        text = stringResource(
            R.string.ayah_reference,
            ayah.surahNameEnglish,
            ayah.surahNumber,
            ayah.ayahNumber,
        ),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

/** Scrolls, so Retry stays reachable with large fonts in landscape. */
@Composable
private fun DailyQuoteError(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DailyQuoteErrorMessage()
        Button(onClick = onRetry) {
            Text(text = stringResource(R.string.daily_quote_retry))
        }
    }
}

@Composable
private fun DailyQuoteErrorMessage() {
    Text(
        text = stringResource(R.string.daily_quote_error),
        // Polite, so TalkBack announces the error without interrupting other speech.
        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
    )
}
