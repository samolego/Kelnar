package io.github.samolego.kelnar.ui.screens

import kotlinx.browser.window

actual fun encodeURIComponent(str: String): String {
    return io.github.samolego.kelnar.encodeURIComponent(str)
}

actual fun getCurrentBaseUrl(): String {
    return "${window.location.protocol}//${window.location.host}${window.location.pathname}"
}
