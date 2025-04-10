package it.innovactors.datetimepicker.time

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import it.innovactors.datetimepicker.commonComponents.CircularListItemPicker
import it.innovactors.datetimepicker.commonComponents.ListItemPicker

@Composable
fun NumberPicker(
    modifier: Modifier = Modifier,
    label: (Int) -> String = { it.toString() },
    value: Int,
    onValueChange: (Int) -> Unit,
    dividersColor: Color = MaterialTheme.colorScheme.primary,
    range: Iterable<Int>,
    textStyle: TextStyle = LocalTextStyle.current,
    itemHeight: Dp = 40.dp,
    cyclic: Boolean = false
) {
    val list = range.toList()
    require(list.isNotEmpty()) { "The range cannot be empty" }
    require(list.contains(value)) { "The value must be within the given range" }

    if (cyclic) {
        CircularListItemPicker(
            modifier = modifier,
            label = label,
            value = value,
            onValueChange = onValueChange,
            dividersColor = dividersColor,
            list = list,
            textStyle = textStyle,
            itemHeight = itemHeight
        )
    } else {
        ListItemPicker(
            modifier = modifier,
            label = label,
            value = value,
            onValueChange = onValueChange,
            dividersColor = dividersColor,
            list = list,
            textStyle = textStyle,
            itemHeight = itemHeight
        )
    }
}