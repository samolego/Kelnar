package io.github.samolego.kelnar.utils

/**
 * Generates a unique ID based on the current collection size.
 * This ensures IDs are simple numeric strings and avoids duplicates during batch operations.
 *
 * @param currentSize The current size of the collection
 * @return A string ID based on the collection size
 */
fun generateId(currentSize: Int): String {
    return (currentSize + 1).toString()
}
