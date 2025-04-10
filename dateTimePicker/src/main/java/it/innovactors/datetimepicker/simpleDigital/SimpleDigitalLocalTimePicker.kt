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
import java.time.LocalTime

/**
 * A four-digit time picker using the SimpleDigitPicker to ensure all values work correctly
 */
@Composable
fun SimpleDigitalTimePicker(
    modifier: Modifier = Modifier,
    initialTime: LocalTime,
    onTimeChanged: (LocalTime) -> Unit,
    isValidTime: (LocalTime) -> Boolean = { true },
    separatorColor: Color = Color.Gray
) {
    // Extract individual digits from initial values
    val initialHoursTens = initialTime.hour / 10
    val initialHoursUnits = initialTime.hour % 10
    val initialMinutesTens = initialTime.minute / 10
    val initialMinutesUnits = initialTime.minute % 10

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

                val newHours = newValue * 10 + hoursUnits
                val newMinutes = minutesTens * 10 + minutesUnits
                val newTime = LocalTime.of(newHours, newMinutes)

                if (isValidTime(newTime)) {
                    onTimeChanged(newTime)
                }
            }
        )

        // Hours units digit (0-9, but 0-3 when tens is 2)
        SimpleDigitPicker(
            value = hoursUnits,
            maxValue = 9,
            onValueChange = { newValue ->
                hoursUnits = newValue

                val newHours = hoursTens * 10 + newValue
                val newMinutes = minutesTens * 10 + minutesUnits
                val newTime = LocalTime.of(newHours, newMinutes)

                if (isValidTime(newTime)) {
                    onTimeChanged(newTime)
                }
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
            color = separatorColor
        )

        // Minutes tens digit (0-5)
        SimpleDigitPicker(
            value = minutesTens,
            maxValue = 5,
            onValueChange = { newValue ->
                minutesTens = newValue

                val newHours = hoursTens * 10 + hoursUnits
                val newMinutes = newValue * 10 + minutesUnits
                val newTime = LocalTime.of(newHours, newMinutes)

                if (isValidTime(newTime)) {
                    onTimeChanged(newTime)
                }
            }
        )

        // Minutes units digit (0-9)
        SimpleDigitPicker(
            value = minutesUnits,
            maxValue = 9,
            onValueChange = { newValue ->
                minutesUnits = newValue

                val newHours = hoursTens * 10 + hoursUnits
                val newMinutes = minutesTens * 10 + newValue
                val newTime = LocalTime.of(newHours, newMinutes)

                if (isValidTime(newTime)) {
                    onTimeChanged(newTime)
                }
            }
        )
    }
}