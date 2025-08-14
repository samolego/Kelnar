package io.github.samolego.kelnar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.samolego.kelnar.data.Order
import io.github.samolego.kelnar.ui.screens.OrderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableOrderCard(
        order: Order,
        onClick: () -> Unit,
        onDeleteOrder: () -> Unit,
        onSwipeAction: () -> Unit,
        swipeActionIcon: ImageVector,
        swipeActionText: String,
        swipeActionColor: Color,
        onSwipeActionColor: Color,
        swipePercentage: Float = 0.6f
) {
    var isActionTriggered by remember { mutableStateOf(false) }
    val swipeableState =
            rememberSwipeToDismissBoxState(
                    confirmValueChange = { dismissDirection ->
                        when (dismissDirection) {
                            SwipeToDismissBoxValue.StartToEnd -> {
                                if (!isActionTriggered) {
                                    onSwipeAction()
                                    isActionTriggered = true
                                }
                                false // Don't dismiss the card
                            }
                            else -> false
                        }
                    },
                    positionalThreshold = { it * swipePercentage }
            )

    // Reset the action trigger when the swipe state resets
    LaunchedEffect(swipeableState.currentValue) {
        if (swipeableState.currentValue == SwipeToDismissBoxValue.Settled) {
            isActionTriggered = false
        }
    }

    SwipeToDismissBox(
            state = swipeableState,
            backgroundContent = {
                // Calculate progress based on the current offset and the size of the container
                val progress =
                        if (swipeableState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                            swipeableState.progress
                        } else {
                            0f
                        }

                Box(
                        modifier =
                                Modifier.fillMaxSize()
                                        .background(swipeActionColor, RoundedCornerShape(12.dp))
                                        .padding(16.dp)
                                        .alpha(progress),
                        contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                                swipeActionIcon,
                                contentDescription = swipeActionText,
                                tint = onSwipeActionColor,
                                modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                                text = swipeActionText,
                                color = onSwipeActionColor,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            enableDismissFromStartToEnd = true,
            enableDismissFromEndToStart = false
    ) {
        OrderCard(
                order = order,
                onClick = onClick,
                onDeleteOrder = onDeleteOrder,
                onMarkCompleted = {},
                hideCompleteButton = true
        )
    }
}
