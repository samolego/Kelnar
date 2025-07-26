package io.github.samolego.kelnar.ui.components

import androidx.compose.runtime.Composable

@Composable
actual fun TextFieldWrapper(content: @Composable () -> Unit) {
    content()
}
