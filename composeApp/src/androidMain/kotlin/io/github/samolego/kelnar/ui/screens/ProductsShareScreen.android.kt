package io.github.samolego.kelnar.ui.screens

import io.github.samolego.kelnar.utils.AppConfig
import java.net.URLEncoder

actual fun encodeURIComponent(str: String): String {
    return URLEncoder.encode(str, "UTF-8")
}

actual fun getCurrentBaseUrl(): String {
    return AppConfig.BASE_URL
}
