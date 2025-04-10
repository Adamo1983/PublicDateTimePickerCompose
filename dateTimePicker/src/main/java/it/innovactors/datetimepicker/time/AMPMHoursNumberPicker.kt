package it.innovactors.datetimepicker.time

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import it.innovactors.datetimepicker.commonComponents.CircularListItemPicker
import it.innovactors.datetimepicker.core.AMPMHours
import kotlin.math.abs

@Composable
fun AMPMHoursNumberPicker(
    modifier: Modifier = Modifier,
    value: AMPMHours,
    leadingZero: Boolean = true,
    hoursRange: Iterable<Int> = (1..12),
    minutesValues: List<Int>? = null, // Lista opzionale di valori minuti permessi
    hoursDivider: (@Composable () -> Unit)? = null,
    minutesDivider: (@Composable () -> Unit)? = null,
    onValueChange: (AMPMHours) -> Unit,
    dividersColor: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = LocalTextStyle.current,
    itemHeight: Dp = 40.dp,
    cyclic: Boolean = true
) {
    // Determina se usare tutti i minuti o solo quelli specificati
    val minutesRange = remember(minutesValues) {
        minutesValues ?: (0..59).toList()
    }

    // Se è specificata una lista di minuti, verifica che il valore corrente sia valido
    val correctedMinutes = if (minutesValues != null && value.minutes !in minutesRange) {
        minutesRange.minByOrNull { abs(it - value.minutes) } ?: 0
    } else {
        value.minutes
    }

    val correctedValue = if (correctedMinutes != value.minutes) {
        value.copy(minutes = correctedMinutes)
    } else {
        value
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Ore
        CircularListItemPicker(
            modifier = Modifier.weight(1f),
            value = correctedValue.hours,
            label = {
                "${if (leadingZero && abs(it) < 10) "0" else ""}$it"
            },
            onValueChange = {
                onValueChange(correctedValue.copy(hours = it))
            },
            dividersColor = dividersColor,
            textStyle = textStyle,
            list = hoursRange.toList(),
            itemHeight = itemHeight
        )

        hoursDivider?.invoke()

        // Minuti
        CircularListItemPicker(
            modifier = Modifier.weight(1f),
            label = {
                "${if (leadingZero && abs(it) < 10) "0" else ""}$it"
            },
            value = correctedValue.minutes,
            onValueChange = {
                onValueChange(correctedValue.copy(minutes = it))
            },
            dividersColor = dividersColor,
            textStyle = textStyle,
            list = minutesRange,
            itemHeight = itemHeight
        )

        minutesDivider?.invoke()

        // AM/PM
        CircularListItemPicker(
            value = when (correctedValue.dayTime) {
                AMPMHours.DayTime.AM -> 0
                else -> 1
            },
            label = {
                when (it) {
                    0 -> "AM"
                    else -> "PM"
                }
            },
            onValueChange = {
                onValueChange(
                    correctedValue.copy(
                        dayTime = when (it) {
                            0 -> AMPMHours.DayTime.AM
                            else -> AMPMHours.DayTime.PM
                        }
                    )
                )
            },
            dividersColor = dividersColor,
            textStyle = textStyle,
            list = listOf(0, 1),
            itemHeight = itemHeight
        )
    }
}