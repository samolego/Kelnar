package io.github.samolego.kelnar.utils

import kotlinx.browser.window

@OptIn(ExperimentalWasmJsInterop::class)
actual fun copyToClipboard(text: String) {
    try {
        // Use the modern clipboard API if available
        window.navigator.clipboard.writeText(text)
    } catch (e: Exception) {
        // Fallback - clipboard API not available
        println("Clipboard API not available, please copy manually: $text")
    }
}
