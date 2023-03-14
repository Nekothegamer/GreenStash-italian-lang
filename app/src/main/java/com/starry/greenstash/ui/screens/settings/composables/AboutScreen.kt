package com.starry.greenstash.ui.screens.settings.composables

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.starry.greenstash.BuildConfig
import com.starry.greenstash.R

sealed class AboutLinks(val url: String) {
    object ReadMe : AboutLinks("https://github.com/Pool-Of-Tears/GreenStash")
    object PrivacyPolicy :
        AboutLinks("https://github.com/Pool-Of-Tears/GreenStash/blob/main/legal/PRIVACY-POLICY.md")

    object GithubIssues : AboutLinks("https://github.com/Pool-Of-Tears/GreenStash/issues/new")
    object Telegram : AboutLinks("https://t.me/PotApps")
}

@ExperimentalMaterial3Api
@Composable
fun AboutScreen(navController: NavController) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(title = {
                Text(
                    stringResource(id = R.string.about_screen_header),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack, contentDescription = null
                    )
                }
            }, scrollBehavior = scrollBehavior, colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    4.dp
                )
            )
            )
        }) {
        LazyColumn(modifier = Modifier.padding(it)) {
            item {
                SettingsItem(title = stringResource(id = R.string.about_readme_title),
                    description = stringResource(id = R.string.about_readme_desc),
                    icon = ImageVector.vectorResource(id = R.drawable.ic_about_readme),
                    onClick = { openWebLink(context, AboutLinks.ReadMe.url) }
                )
            }
            item {
                SettingsItem(title = stringResource(id = R.string.about_privacy_title),
                    description = stringResource(id = R.string.about_privacy_desc),
                    icon = ImageVector.vectorResource(id = R.drawable.ic_about_privacy),
                    onClick = { openWebLink(context, AboutLinks.PrivacyPolicy.url) }
                )
            }
            item {
                SettingsItem(title = stringResource(id = R.string.about_gh_issue_title),
                    description = stringResource(id = R.string.about_gh_issue_desc),
                    icon = ImageVector.vectorResource(id = R.drawable.ic_about_gh_issue),
                    onClick = { openWebLink(context, AboutLinks.GithubIssues.url) }
                )
            }
            item {
                SettingsItem(title = stringResource(id = R.string.about_telegram_title),
                    description = stringResource(id = R.string.about_telegram_desc),
                    icon = ImageVector.vectorResource(id = R.drawable.ic_about_telegram),
                    onClick = { openWebLink(context, AboutLinks.Telegram.url) }
                )
            }
            item {
                SettingsItem(title = stringResource(id = R.string.about_version_title),
                    description = stringResource(id = R.string.about_version_desc).format(
                        BuildConfig.VERSION_NAME
                    ),
                    icon = ImageVector.vectorResource(id = R.drawable.ic_about_version),
                    onClick = { clipboardManager.setText(AnnotatedString(getVersionReport())) }
                )
            }
        }
    }
}

fun openWebLink(context: Context, url: String) {
    val uri: Uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    try {
        context.startActivity(intent)
    } catch (exc: ActivityNotFoundException) {
        exc.printStackTrace()
    }
}

fun getVersionReport(): String {
    val versionName = BuildConfig.VERSION_NAME
    val versionCode = BuildConfig.VERSION_CODE
    val release = if (Build.VERSION.SDK_INT >= 30) {
        Build.VERSION.RELEASE_OR_CODENAME
    } else {
        Build.VERSION.RELEASE
    }
    return StringBuilder().append("App version: $versionName ($versionCode)\n")
        .append("Android Version: Android $release (API ${Build.VERSION.SDK_INT})\n")
        .append("Device information: ${Build.MANUFACTURER} ${Build.MODEL} (${Build.DEVICE})\n")
        .append("Supported ABIs: ${Build.SUPPORTED_ABIS.contentToString()}\n")
        .toString()
}
