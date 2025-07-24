package io.github.samolego.kelnar.utils

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

actual fun copyToClipboard(text: String) {
    try {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val stringSelection = StringSelection(text)
        clipboard.setContents(stringSelection, null)
    } catch (e: Exception) {
        println("Failed to copy to clipboard: ${e.message}")
    }
}
