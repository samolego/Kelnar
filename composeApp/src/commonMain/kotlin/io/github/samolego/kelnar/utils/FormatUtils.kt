package io.github.samolego.kelnar.utils

import kotlinx.datetime.LocalDateTime
import kotlin.math.abs
import kotlin.math.round

object FormatUtils {
    fun formatCurrency(amount: Double): String {
        val roundedCents = round(amount * 100).toLong()
        val wholePart = roundedCents / 100
        val fractionalPart = abs(roundedCents % 100)
        return "$wholePart.${fractionalPart.toString().padStart(2, '0')}"
    }

    fun formatPrice(price: Double): String = "${formatCurrency(price)} €"

    fun formatTime(dateTime: LocalDateTime): String {
        val hour = dateTime.hour.toString().padStart(2, '0')
        val minute = dateTime.minute.toString().padStart(2, '0')
        return "$hour:$minute"
    }
}

// Extension function for Double to format as currency
fun Double.formatAsPrice(): String = FormatUtils.formatPrice(this)

// Extension function for LocalDateTime to format as time
fun LocalDateTime.formatAsTime(): String = FormatUtils.formatTime(this)
