package io.github.samolego.kelnar

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import kotlinx.browser.document
import kotlinx.serialization.ExperimentalSerializationApi

external fun decodeURIComponent(encodedURI: String): String

external fun encodeURIComponent(str: String): String

@OptIn(
        ExperimentalComposeUiApi::class,
        ExperimentalSerializationApi::class,
        ExperimentalBrowserHistoryApi::class
)
fun main() {
    ComposeViewport(document.body!!) {
        App(onNavHostReady = { navController -> navController.bindToBrowserNavigation() })
    }
}
