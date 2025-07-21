package io.github.samolego.kelnar

import io.github.samolego.kelnar.repository.LocalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.Properties

class DesktopLocalStorage : LocalStorage {

    private val dataDir = File(System.getProperty("user.home"), ".kelnar")
    private val dataFile = File(dataDir, "data.properties")
    private val properties = Properties()

    init {
        if (!dataDir.exists()) {
            dataDir.mkdirs()
        }

        if (dataFile.exists()) {
            try {
                dataFile.inputStream().use { input ->
                    properties.load(input)
                }
            } catch (e: IOException) {
                // Handle error silently, start with empty properties
            }
        }
    }

    override suspend fun getString(key: String): String? = withContext(Dispatchers.IO) {
        properties.getProperty(key)
    }

    override suspend fun putString(key: String, value: String) = withContext(Dispatchers.IO) {
        properties.setProperty(key, value)
        saveProperties()
    }

    override suspend fun remove(key: String) = withContext(Dispatchers.IO) {
        properties.remove(key)
        saveProperties()
    }

    override suspend fun clear() = withContext(Dispatchers.IO) {
        properties.clear()
        saveProperties()
    }

    private fun saveProperties() {
        try {
            dataFile.outputStream().use { output ->
                properties.store(output, "Kelnar App Data")
            }
        } catch (e: IOException) {
            // Handle error silently
        }
    }
}

actual fun getLocalStorage(): LocalStorage {
    return DesktopLocalStorage()
}
