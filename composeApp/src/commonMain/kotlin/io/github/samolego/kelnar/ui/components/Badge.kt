package io.github.samolego.kelnar.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun Badge(
        text: String,
        icon: ImageVector = Icons.Default.CheckCircle,
        backgroundColor: Color = MaterialTheme.colorScheme.primary,
        contentColor: Color =  MaterialTheme.colorScheme.onPrimary,
        modifier: Modifier = Modifier
) {
    Surface(color = backgroundColor, shape = MaterialTheme.shapes.small, modifier = modifier) {
        Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text, style = MaterialTheme.typography.labelMedium, color = contentColor)
        }
    }
}

@Composable
fun CompletedBadge(modifier: Modifier = Modifier) {
    Badge(
            text = "Completed",
            icon = Icons.Default.CheckCircle,
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = modifier
    )
}
