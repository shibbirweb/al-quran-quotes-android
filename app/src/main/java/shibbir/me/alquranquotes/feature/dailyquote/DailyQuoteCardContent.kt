package shibbir.me.alquranquotes.feature.dailyquote

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import shibbir.me.alquranquotes.R
import shibbir.me.alquranquotes.ui.text.rememberArabicAnnotatedText

/** The daily quote in a Material 3 card: labels, the quote itself, and its reference. */
@Composable
internal fun DailyQuoteCardContent(
    dailyQuoteCard: DailyQuoteCard,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (dailyQuoteCard) {
                is DailyQuoteCard.AyahCard -> AyahCardBody(ayahCard = dailyQuoteCard)
                is DailyQuoteCard.FreeTextCard -> FreeTextCardBody(freeTextCard = dailyQuoteCard)
            }
        }
    }
}

@Composable
private fun AyahCardBody(ayahCard: DailyQuoteCard.AyahCard) {
    DailyQuoteLabels(
        titleResId = R.string.daily_quote_title,
        originNote = ayahCard.originNote,
    )
    AyahArabicText(arabicText = ayahCard.arabicText)
    HorizontalDivider()
    QuoteTranslation(translation = ayahCard.translation)
    QuoteReference(reference = ayahReference(ayahCard))
}

@Composable
private fun FreeTextCardBody(freeTextCard: DailyQuoteCard.FreeTextCard) {
    DailyQuoteLabels(
        titleResId = R.string.daily_quote_free_text_title,
        originNote = freeTextCard.originNote,
    )
    FreeTextQuote(text = freeTextCard.text)
    val reference = freeTextCard.reference
    if (reference != null) {
        HorizontalDivider()
        QuoteReference(reference = reference)
    }
}

/** The card's heading, and a small note under it such as "Your quote" when there is one. */
@Composable
private fun DailyQuoteLabels(
    @StringRes titleResId: Int,
    originNote: DailyQuoteOriginNote?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(titleResId),
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        if (originNote != null) {
            OriginNoteLabel(originNote = originNote)
        }
    }
}

@Composable
private fun OriginNoteLabel(originNote: DailyQuoteOriginNote) {
    Text(
        text = stringResource(originNote.labelResId),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.tertiary,
    )
}

@Composable
private fun AyahArabicText(arabicText: String) {
    val arabicAnnotatedText = rememberArabicAnnotatedText(arabicText)
    val headlineStyle = MaterialTheme.typography.headlineMedium
    val arabicTextStyle = headlineStyle.copy(
        textDirection = TextDirection.Rtl,
        // Relative, so the line height follows non-linear font scaling.
        lineHeight = 2.em,
    )
    Text(
        text = arabicAnnotatedText,
        modifier = Modifier.fillMaxWidth(),
        style = arabicTextStyle,
        // Start is the right edge, because the text direction is right to left.
        textAlign = TextAlign.Start,
    )
}

@Composable
private fun QuoteTranslation(translation: String) {
    Text(
        text = translation,
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Composable
private fun FreeTextQuote(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
    )
}

@Composable
private fun QuoteReference(reference: String) {
    Text(
        text = reference,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun ayahReference(ayahCard: DailyQuoteCard.AyahCard): String {
    return stringResource(
        R.string.ayah_reference,
        ayahCard.surahName,
        ayahCard.surahNumber,
        ayahCard.ayahNumber,
    )
}
