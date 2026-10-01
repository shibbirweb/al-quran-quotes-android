package shibbir.me.alquranquotes.feature.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import shibbir.me.alquranquotes.R
import shibbir.me.alquranquotes.ui.components.TopLevelTopAppBar
import shibbir.me.alquranquotes.ui.preview.LightDarkLargeFontPreviews
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

/**
 * Placeholder Settings screen: display-only sample rows under a large top app bar that
 * collapses as the rows scroll. Nothing here can be changed yet, so it is a static, stateless
 * screen with no ViewModel; one comes with the first real setting.
 */
// The top app bar scroll behavior is still experimental in Material 3 1.4.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopLevelTopAppBar(
                title = stringResource(R.string.settings_title),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        SettingsRows(modifier = Modifier.padding(innerPadding))
    }
}

/** Scrolls, so every row stays reachable with large fonts and the top app bar collapses. */
@Composable
private fun SettingsRows(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        SettingsRow(
            headlineResId = R.string.settings_theme_title,
            supportingText = stringResource(R.string.settings_theme_value),
        )
        SettingsRow(
            headlineResId = R.string.settings_text_size_title,
            supportingText = stringResource(R.string.settings_text_size_value),
        )
        AboutRow()
        ComingSoonText()
    }
}

@Composable
private fun AboutRow() {
    val appName = stringResource(R.string.app_name)
    val versionName = rememberInstalledVersionName()
    SettingsRow(
        headlineResId = R.string.settings_about_title,
        supportingText = stringResource(R.string.settings_about_value, appName, versionName),
    )
}

@Composable
private fun SettingsRow(@StringRes headlineResId: Int, supportingText: String) {
    ListItem(
        headlineContent = { Text(text = stringResource(headlineResId)) },
        supportingContent = { Text(text = supportingText) },
    )
}

@Composable
private fun ComingSoonText() {
    Text(
        text = stringResource(R.string.settings_coming_soon),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

/** The installed app's version name, for example "1.0". */
@Composable
private fun rememberInstalledVersionName(): String {
    val context = LocalContext.current
    return remember(context) {
        val packageManager = context.packageManager
        // The flags overload needs API 33; this one still works on every version.
        @Suppress("DEPRECATION")
        val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName.orEmpty()
    }
}

@LightDarkLargeFontPreviews
@Composable
private fun SettingsScreenPreview() {
    AlQuranQuotesTheme {
        SettingsScreen()
    }
}
