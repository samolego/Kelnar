package io.github.samolego.kelnar.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import io.github.samolego.kelnar.AppContext

actual fun copyToClipboard(text: String) {
    val context = AppContext.get()
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("Shared Products", text)
    clipboardManager.setPrimaryClip(clipData)
}
