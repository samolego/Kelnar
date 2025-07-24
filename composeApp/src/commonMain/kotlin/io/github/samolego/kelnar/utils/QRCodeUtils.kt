package io.github.samolego.kelnar.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    val qrCode =
            remember(data, foregroundColor, backgroundColor) {
                try {
                    QRCode.ofSquares()
                            .withColor(Colors.BLACK)
                            .withBackgroundColor(Colors.WHITE)
                            .withSize(25)
                            .build(data)
                } catch (e: Exception) {
                    println("Error generating QR code: ${e.message}")
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
        qrCode?.let { qr ->
            Canvas(modifier = Modifier.size((size - 16).dp)) {
                val rawData = qr.rawData
                val matrixSize = rawData.size
                val cellSize = this.size.width / matrixSize

                // Draw background
                drawRect(color = backgroundColor, size = this.size)

                // Draw QR code cells
                for (row in rawData.indices) {
                    for (col in rawData[row].indices) {
                        if (rawData[row][col].dark) {
                            drawRect(
                                    color = foregroundColor,
                                    topLeft = Offset(col * cellSize, row * cellSize),
                                    size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                            )
                        }
                    }
                }
            }
        }
    }
}
