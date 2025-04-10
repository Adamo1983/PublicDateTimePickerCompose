package it.innovactors.datetimepicker.commonComponents

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign


@Composable
fun Label(text: String, modifier: Modifier) {
    Text(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(onLongPress = {
                // Empty to disable text selection
            })
        },
        text = text,
        textAlign = TextAlign.Center,
    )
}