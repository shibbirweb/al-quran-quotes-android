package shibbir.me.alquranquotes.feature.quotes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import shibbir.me.alquranquotes.R
import shibbir.me.alquranquotes.ui.text.rememberArabicAnnotatedText

/**
 * One quote in the Quotes list: its kind, a short preview, its reference, and Edit and Delete
 * buttons. Every quote, bundled or the user's own, can be edited and deleted.
 */
@Composable
internal fun QuoteListCardContent(
    quoteCard: QuoteListCard,
    onEditQuote: (quoteId: Long) -> Unit,
    onDeleteQuote: (quoteId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 4.dp, bottom = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            QuoteCardBody(quoteCard = quoteCard)
            QuoteActions(
                onEdit = { onEditQuote(quoteCard.quoteId) },
                onDelete = { onDeleteQuote(quoteCard.quoteId) },
            )
        }
    }
}

/** Keeps the text clear of the card's right edge, which the action buttons do not need. */
@Composable
private fun QuoteCardBody(quoteCard: QuoteListCard) {
    Column(
        modifier = Modifier.padding(end = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        QuoteKindLabel(quoteListKind = quoteCard.kind)
        when (quoteCard) {
            is QuoteListCard.AyahCard -> AyahPreview(ayahCard = quoteCard)
            is QuoteListCard.FreeTextCard -> FreeTextPreview(freeTextCard = quoteCard)
        }
    }
}

@Composable
private fun QuoteKindLabel(quoteListKind: QuoteListKind) {
    Text(
        text = stringResource(quoteListKind.labelResId),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun AyahPreview(ayahCard: QuoteListCard.AyahCard) {
    ArabicLine(arabicText = ayahCard.arabicText)
    PreviewText(text = ayahCard.translation, maxLines = 3)
    val reference = stringResource(
        R.string.quotes_ayah_reference,
        ayahCard.surahName,
        ayahCard.surahNumber,
        ayahCard.ayahNumber,
    )
    QuoteReference(reference = reference)
}

@Composable
private fun FreeTextPreview(freeTextCard: QuoteListCard.FreeTextCard) {
    PreviewText(text = freeTextCard.text, maxLines = 4)
    val reference = freeTextCard.reference
    if (reference != null) {
        QuoteReference(reference = reference)
    }
}

@Composable
private fun ArabicLine(arabicText: String) {
    val arabicAnnotatedText = rememberArabicAnnotatedText(arabicText)
    Text(
        text = arabicAnnotatedText,
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.titleLarge.copy(textDirection = TextDirection.Rtl),
        // Start is the right edge, because the text direction is right to left.
        textAlign = TextAlign.Start,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun PreviewText(text: String, maxLines: Int) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
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
private fun QuoteActions(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        IconButton(onClick = onEdit) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.quotes_edit),
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.quotes_delete),
            )
        }
    }
}
