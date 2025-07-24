package io.github.samolego.kelnar.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

actual fun copyToClipboard(text: String) {
    // This will be called from a Composable context, so we need to get context differently
    // For now, we'll provide a simpler implementation that can be enhanced later
    println("Android clipboard: $text")
}

@Composable
fun copyToClipboardAndroid(text: String) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("Shared Products", text)
    clipboardManager.setPrimaryClip(clipData)
}
