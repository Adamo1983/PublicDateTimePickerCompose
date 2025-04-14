package it.innovactors.datetimepicker.core

import androidx.compose.animation.core.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Gets the circular index for the given offset, supports circular scrolling
 */
fun <T> getCircularItemIndexForOffset(
    list: List<T>,
    value: T,
    offset: Float,
    halfNumbersColumnHeightPx: Float
): Int {
    val currentIndex = list.indexOf(value)
    val offsetIndex = (offset / halfNumbersColumnHeightPx).toInt()

    // Calculate the new index with circular wrapping
    var newIndex = (currentIndex - offsetIndex) % list.size
    // Handle negative indices
    if (newIndex < 0) newIndex += list.size

    return newIndex
}

suspend fun Animatable<Float, AnimationVector1D>.fling(
    initialVelocity: Float,
    animationSpec: DecayAnimationSpec<Float>,
    adjustTarget: ((Float) -> Float)?,
    block: (Animatable<Float, AnimationVector1D>.() -> Unit)? = null,
): AnimationResult<Float, AnimationVector1D> {
    val targetValue = animationSpec.calculateTargetValue(value, initialVelocity)
    val adjustedTarget = adjustTarget?.invoke(targetValue)
    return if (adjustedTarget != null) {
        animateTo(
            targetValue = adjustedTarget,
            initialVelocity = initialVelocity,
            block = block
        )
    } else {
        animateDecay(
            initialVelocity = initialVelocity,
            animationSpec = animationSpec,
            block = block,
        )
    }
}

fun LocalDate.formatWithSpecialLabels(
    formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM),
    todayLabel: String = "Oggi",
    yesterdayLabel: String = "Ieri"
): String {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    return when {
        this.isEqual(today) -> todayLabel
        this.isEqual(yesterday) -> yesterdayLabel
        else -> this.format(formatter)
    }
}

fun LocalTime.toFullHours(): FullHours{
    return FullHours(this.hour, this.minute)
}

fun FullHours.toLocalTime(): LocalTime{
    return LocalTime.of(this.hours, this.minutes)
}