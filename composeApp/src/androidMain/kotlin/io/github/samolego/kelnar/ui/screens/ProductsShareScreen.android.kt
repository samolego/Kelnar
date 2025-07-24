package io.github.samolego.kelnar.ui.screens

import java.net.URLEncoder
import io.github.samolego.kelnar.utils.AppConfig

actual fun encodeURIComponent(str: String): String {
    return URLEncoder.encode(str, "UTF-8")
}

actual fun getCurrentBaseUrl(): String {
    return AppConfig.BASE_URL
}
