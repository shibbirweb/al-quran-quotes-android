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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import shibbir.me.alquranquotes.R
import shibbir.me.alquranquotes.model.Ayah
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

const val DAILY_QUOTE_LOADING_TAG = "daily_quote_loading"

@Composable
fun DailyQuoteRoute(
    modifier: Modifier = Modifier,
    viewModel: DailyQuoteViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DailyQuoteScreen(
        uiState = uiState,
        onRetry = viewModel::loadDailyQuote,
        modifier = modifier,
    )
}

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
            DailyQuoteUiState.Loading -> CircularProgressIndicator(
                modifier = Modifier.testTag(DAILY_QUOTE_LOADING_TAG),
            )
            DailyQuoteUiState.Error -> DailyQuoteError(onRetry = onRetry)
            is DailyQuoteUiState.Success -> DailyAyah(ayah = uiState.ayah)
        }
    }
}

@Composable
private fun DailyAyah(ayah: Ayah) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            text = stringResource(R.string.daily_quote_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = ayah.arabicText,
            style = MaterialTheme.typography.headlineSmall.copy(
                textDirection = TextDirection.Rtl,
                lineHeight = 48.sp,
            ),
            textAlign = TextAlign.Center,
        )
        Text(
            text = ayah.translation,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
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
}

@Composable
private fun DailyQuoteError(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.daily_quote_error),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Button(onClick = onRetry) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DailyQuoteScreenSuccessPreview() {
    AlQuranQuotesTheme {
        DailyQuoteScreen(
            uiState = DailyQuoteUiState.Success(
                Ayah(
                    surahNumber = 94,
                    ayahNumber = 5,
                    surahNameEnglish = "Ash-Sharh",
                    surahNameArabic = "سورة الشرح",
                    arabicText = "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا",
                    translation = "For indeed, with hardship [will be] ease.",
                ),
            ),
            onRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DailyQuoteScreenErrorPreview() {
    AlQuranQuotesTheme {
        DailyQuoteScreen(uiState = DailyQuoteUiState.Error, onRetry = {})
    }
}
