package it.innovactors.datetimepicker.simpleDigital

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * A simple digit picker with up/down buttons that guarantees all values work correctly
 */
@Composable
fun SimpleDigitPicker(
    modifier: Modifier = Modifier,
    value: Int,
    maxValue: Int,
    onValueChange: (Int) -> Unit,
    additionalConstraint: ((Int) -> Boolean)? = null,
    borderColor: Color = Color.LightGray,
    backgroundColor: Color = Color.White,
    buttonColor: Color = Color.Gray,
    textColor: Color = Color.Black
) {
    Column(
        modifier = modifier
            .width(60.dp)
            .height(250.dp)
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Up button
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .clickable {
                    // Increment with wraparound
                    var newValue = if (value >= maxValue) 0 else value + 1

                    // Ensure it passes additional constraint if provided
                    if (additionalConstraint != null) {
                        var attempts = 0
                        while (!additionalConstraint(newValue) && attempts < maxValue + 1) {
                            newValue = if (newValue >= maxValue) 0 else newValue + 1
                            attempts++
                        }
                    }

                    onValueChange(newValue)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = "Increment",
                tint = buttonColor
            )
        }

        // Current value
        Box(
            modifier = Modifier
                .weight(2f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString(),
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }

        // Down button
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .clickable {
                    // Decrement with wraparound
                    var newValue = if (value <= 0) maxValue else value - 1

                    // Ensure it passes additional constraint if provided
                    if (additionalConstraint != null) {
                        var attempts = 0
                        while (!additionalConstraint(newValue) && attempts < maxValue + 1) {
                            newValue = if (newValue <= 0) maxValue else newValue - 1
                            attempts++
                        }
                    }

                    onValueChange(newValue)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = "Decrement",
                tint = buttonColor
            )
        }
    }
}