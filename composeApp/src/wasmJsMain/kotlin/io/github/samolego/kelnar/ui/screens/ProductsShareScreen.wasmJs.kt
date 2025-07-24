package io.github.samolego.kelnar.ui.screens

import kotlinx.browser.window
import io.github.samolego.kelnar.utils.AppConfig

actual fun encodeURIComponent(str: String): String {
    return io.github.samolego.kelnar.encodeURIComponent(str)
}

actual fun getCurrentBaseUrl(): String {
    return AppConfig.BASE_URL
}
