package io.github.samolego.kelnar

import android.content.Context
import android.content.SharedPreferences
import io.github.samolego.kelnar.repository.LocalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidLocalStorage(private val context: Context) : LocalStorage {

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences("kelnar_prefs", Context.MODE_PRIVATE)
    }

    override suspend fun getString(key: String): String? = withContext(Dispatchers.IO) {
        preferences.getString(key, null)
    }

    override suspend fun putString(key: String, value: String) = withContext(Dispatchers.IO) {
        preferences.edit()
            .putString(key, value)
            .apply()
    }

    override suspend fun remove(key: String) = withContext(Dispatchers.IO) {
        preferences.edit()
            .remove(key)
            .apply()
    }

    override suspend fun clear() = withContext(Dispatchers.IO) {
        preferences.edit()
            .clear()
            .apply()
    }
}

actual fun getLocalStorage(): LocalStorage {
    return AndroidLocalStorage(AppContext.get())
}
