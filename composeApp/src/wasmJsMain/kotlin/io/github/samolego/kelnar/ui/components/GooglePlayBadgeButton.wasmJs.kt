package io.github.samolego.kelnar.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kelnar.composeapp.generated.resources.GetItOnGooglePlay_Badge_Web_color_English
import kelnar.composeapp.generated.resources.GetItOnGooglePlay_Badge_Web_color_Slovenian
import kelnar.composeapp.generated.resources.Res
import kotlinx.browser.window
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun WebDrawGooglePlayBadge() {
    val languageCode = remember { getCurrentLanguageCode() }
    val playBadgeResource =
            when (languageCode) {
                "sl" -> Res.drawable.GetItOnGooglePlay_Badge_Web_color_Slovenian
                else -> Res.drawable.GetItOnGooglePlay_Badge_Web_color_English
            }

    Image(
            painter = painterResource(playBadgeResource),
            contentDescription = "Get it on Google Play",
            modifier =
                    Modifier.padding(16.dp)
                            .size(width = 200.dp, height = 60.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                openUrl(
                                        "https://play.google.com/store/apps/details?id=io.github.samolego.kelnar"
                                )
                            },
            contentScale = ContentScale.Fit
    )
}

private fun openUrl(url: String) {
    window.open(url, "_blank")
}

private fun getCurrentLanguageCode(): String {
    return window.navigator.language.split("-")[0].lowercase()
}
