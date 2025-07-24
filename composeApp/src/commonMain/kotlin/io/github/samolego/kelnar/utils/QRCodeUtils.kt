package io.github.samolego.kelnar.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.decodeToImageBitmap
import qrcode.QRCode
import qrcode.color.Colors

@Composable
fun QRCodeImage(
        data: String,
        size: Int = 200,
        backgroundColor: Color = Color.White,
        foregroundColor: Color = Color.Black,
        modifier: Modifier = Modifier
) {
    val qrCodeImageBitmap =
            remember(data, foregroundColor, backgroundColor) {
                try {
                    generateQRCodeImageBitmap(data, foregroundColor, backgroundColor)
                } catch (e: Exception) {
                    null
                }
            }

    Box(
            modifier =
                    modifier.size(size.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(backgroundColor)
                            .padding(8.dp)
    ) {
        qrCodeImageBitmap?.let { imageBitmap ->
            Image(
                    bitmap = imageBitmap,
                    contentDescription = "QR Code for: $data",
                    modifier = Modifier.size((size - 16).dp)
            )
        }
    }
}

private fun generateQRCodeImageBitmap(
        data: String,
        foregroundColor: Color,
        backgroundColor: Color
): ImageBitmap? {
    return try {
        val qrCode =
                QRCode.ofSquares()
                        .withColor(Colors.BLACK)
                        .withBackgroundColor(Colors.WHITE)
                        .withSize(25)
                        .build(data)

        qrCode.renderToBytes().decodeToImageBitmap()
    } catch (e: Exception) {
        println("Error generating QR code: ${e.message}")
        null
    }
}
