package it.innovactors.datetimepicker.commonComponents

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import it.innovactors.datetimepicker.core.fling
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt



@Composable
fun <T> ListItemPicker(
    modifier: Modifier = Modifier,
    label: (T) -> String = { it.toString() },
    value: T,
    onValueChange: (T) -> Unit,
    dividersColor: Color = MaterialTheme.colorScheme.primary,
    list: List<T>,
    textStyle: TextStyle = LocalTextStyle.current,
    itemHeight: Dp = 40.dp
) {
    require(list.isNotEmpty()) { "The list cannot be empty" }
    require(list.contains(value)) { "The value must be within the given list" }

    val minimumAlpha = 0.3f
    val verticalMargin = 8.dp
    val numbersColumnHeight = itemHeight * 2
    val halfNumbersColumnHeight = numbersColumnHeight / 2
    val halfNumbersColumnHeightPx = with(LocalDensity.current) { halfNumbersColumnHeight.toPx() }

    val coroutineScope = rememberCoroutineScope()
    val animatedOffset = remember { Animatable(0f) }

    val coercedAnimatedOffset = animatedOffset.value % halfNumbersColumnHeightPx
    val indexOfElement = list.indexOf(value)

    var dividersWidth by remember { mutableStateOf(0.dp) }

    Layout(
        modifier = modifier
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { deltaY ->
                    coroutineScope.launch {
                        animatedOffset.snapTo(animatedOffset.value + deltaY)
                    }
                },
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        val endValue = animatedOffset.fling(
                            initialVelocity = velocity,
                            animationSpec = exponentialDecay(frictionMultiplier = 20f),
                            adjustTarget = { target ->
                                val coercedTarget = target % halfNumbersColumnHeightPx
                                val coercedAnchors = listOf(-halfNumbersColumnHeightPx, 0f, halfNumbersColumnHeightPx)
                                val coercedPoint = coercedAnchors.minByOrNull { abs(it - coercedTarget) }!!
                                val base = halfNumbersColumnHeightPx * (target / halfNumbersColumnHeightPx).toInt()
                                coercedPoint + base
                            }
                        ).endState.value

                        // Calculate the new index
                        val offsetIndex = (endValue / halfNumbersColumnHeightPx).toInt()
                        val newIndex = indexOfElement - offsetIndex

                        // Handle wrapping
                        val boundedIndex = when {
                            newIndex < 0 -> 0
                            newIndex >= list.size -> list.size - 1
                            else -> newIndex
                        }

                        onValueChange(list[boundedIndex])
                        animatedOffset.snapTo(0f)
                    }
                }
            )
            .padding(vertical = numbersColumnHeight / 3 + verticalMargin * 2),
        content = {
            Box(
                Modifier
                    .width(dividersWidth)
                    .height(2.dp)
                    .background(color = dividersColor)
            )
            Box(
                modifier = Modifier
                    .padding(vertical = verticalMargin, horizontal = 20.dp)
                    .offset { IntOffset(x = 0, y = coercedAnimatedOffset.roundToInt()) }
            ) {
                val baseLabelModifier = Modifier.align(Alignment.Center)
                ProvideTextStyle(textStyle) {
                    // Previous item
                    if (indexOfElement > 0) {
                        Label(
                            text = label(list[indexOfElement - 1]),
                            modifier = baseLabelModifier
                                .offset(y = -halfNumbersColumnHeight)
                                .alpha(maxOf(minimumAlpha, coercedAnimatedOffset / halfNumbersColumnHeightPx))
                        )
                    }

                    // Current item
                    Label(
                        text = label(list[indexOfElement]),
                        modifier = baseLabelModifier
                            .alpha(
                                maxOf(
                                    minimumAlpha,
                                    1 - abs(coercedAnimatedOffset) / halfNumbersColumnHeightPx
                                )
                            )
                    )

                    // Next item
                    if (indexOfElement < list.size - 1) {
                        Label(
                            text = label(list[indexOfElement + 1]),
                            modifier = baseLabelModifier
                                .offset(y = halfNumbersColumnHeight)
                                .alpha(maxOf(minimumAlpha, -coercedAnimatedOffset / halfNumbersColumnHeightPx))
                        )
                    }
                }
            }
            Box(
                Modifier
                    .width(dividersWidth)
                    .height(2.dp)
                    .background(color = dividersColor)
            )
        }
    ) { measurables, constraints ->
        // Don't constrain child views further, measure them with given constraints
        val placeables = measurables.map { measurable ->
            // Measure each children
            measurable.measure(constraints)
        }

        dividersWidth = placeables
            .drop(1)
            .first()
            .width
            .toDp()

        // Set the size of the layout as big as it can
        layout(dividersWidth.toPx().toInt(), placeables.sumOf { it.height }) {
            // Track the y co-ord we have placed children up to
            var yPosition = 0

            // Place children in the parent layout
            placeables.forEach { placeable ->
                // Position item on the screen
                placeable.placeRelative(x = 0, y = yPosition)

                // Record the y co-ord placed up to
                yPosition += placeable.height
            }
        }
    }
}