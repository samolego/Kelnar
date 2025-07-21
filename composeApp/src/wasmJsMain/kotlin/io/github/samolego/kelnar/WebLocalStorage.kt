package io.github.samolego.kelnar

import io.github.samolego.kelnar.repository.LocalStorage
import kotlinx.browser.localStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WebLocalStorage : LocalStorage {

    override suspend fun getString(key: String): String? = withContext(Dispatchers.Default) {
        try {
            localStorage.getItem(key)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun putString(key: String, value: String) = withContext(Dispatchers.Default) {
        try {
            localStorage.setItem(key, value)
        } catch (e: Exception) {
            // Handle error silently - localStorage might be full or disabled
        }
    }

    override suspend fun remove(key: String) = withContext(Dispatchers.Default) {
        try {
            localStorage.removeItem(key)
        } catch (e: Exception) {
            // Handle error silently
        }
    }

    override suspend fun clear() = withContext(Dispatchers.Default) {
        try {
            localStorage.clear()
        } catch (e: Exception) {
            // Handle error silently
        }
    }
}

actual fun getLocalStorage(): LocalStorage {
    return WebLocalStorage()
}
