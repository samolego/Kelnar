package io.github.samolego.kelnar.utils

import kotlin.math.round

object FormatUtils {
    fun formatCurrency(amount: Double): String {
        val rounded = round(amount * 100) / 100
        val wholePart = rounded.toLong()
        val fractionalPart = ((rounded - wholePart) * 100).toLong()
        return "$wholePart.${fractionalPart.toString().padStart(2, '0')}"
    }

    fun formatPrice(price: Double): String = "${formatCurrency(price)} €"
}

// Extension function for Double to format as currency
fun Double.formatAsCurrency(): String = FormatUtils.formatCurrency(this)
fun Double.formatAsPrice(): String = FormatUtils.formatPrice(this)
