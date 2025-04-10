package it.innovactors.datetimepicker.simpleDigital

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * A four-digit time picker using the new SimpleDigitPicker to ensure all values work correctly
 */
@Composable
fun SimpleDigitalTimePicker(
    modifier: Modifier = Modifier,
    initialHours: Int = 0,
    initialMinutes: Int = 0,
    onTimeChanged: (hours: Int, minutes: Int) -> Unit
) {
    // Extract individual digits from initial values
    val initialHoursTens = initialHours / 10
    val initialHoursUnits = initialHours % 10
    val initialMinutesTens = initialMinutes / 10
    val initialMinutesUnits = initialMinutes % 10

    // State for each digit
    var hoursTens by remember { mutableIntStateOf(initialHoursTens) }
    var hoursUnits by remember { mutableIntStateOf(initialHoursUnits) }
    var minutesTens by remember { mutableIntStateOf(initialMinutesTens) }
    var minutesUnits by remember { mutableIntStateOf(initialMinutesUnits) }

    // Row containing all four digit pickers with spacing
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hours tens digit (0-2)
        SimpleDigitPicker(
            value = hoursTens,
            maxValue = 2,
            onValueChange = { newValue ->
                hoursTens = newValue

                // Ensure hours units is valid when tens is 2
                if (newValue == 2 && hoursUnits > 3) {
                    hoursUnits = 3
                }

                onTimeChanged(newValue * 10 + hoursUnits, minutesTens * 10 + minutesUnits)
            }
        )

        // Hours units digit (0-9, but 0-3 when tens is 2)
        SimpleDigitPicker(
            value = hoursUnits,
            maxValue = 9,
            onValueChange = { newValue ->
                hoursUnits = newValue
                onTimeChanged(hoursTens * 10 + newValue, minutesTens * 10 + minutesUnits)
            },
            additionalConstraint = { digit ->
                // Only allow 0-3 when tens is 2
                hoursTens != 2 || digit <= 3
            }
        )

        // Separator between hours and minutes
        Text(
            text = ":",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            //modifier = Modifier.padding(horizontal = 2.dp)
        )

        // Minutes tens digit (0-5)
        SimpleDigitPicker(
            value = minutesTens,
            maxValue = 5,
            onValueChange = { newValue ->
                minutesTens = newValue
                onTimeChanged(hoursTens * 10 + hoursUnits, newValue * 10 + minutesUnits)
            }
        )

        // Minutes units digit (0-9)
        SimpleDigitPicker(
            value = minutesUnits,
            maxValue = 9,
            onValueChange = { newValue ->
                minutesUnits = newValue
                onTimeChanged(hoursTens * 10 + hoursUnits, minutesTens * 10 + newValue)
            }
        )
    }
}