package it.innovactors.datetimepicker.date

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import it.innovactors.datetimepicker.commonComponents.CircularListItemPicker
import it.innovactors.datetimepicker.commonComponents.ListItemPicker
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.abs

/**
 * Un DatePicker che consente di selezionare date all'interno di un intervallo specificato
 * rispetto alla data corrente.
 */
@Composable
fun ScrollDatePicker(
    modifier: Modifier = Modifier,
    value: LocalDate,
    onValueChange: (LocalDate) -> Unit,
    daysBackward: Int = 7,  // Default: 7 giorni indietro
    daysForward: Int = 0,   // Default: 0 giorni avanti
    dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM),
    todayLabel: String = "Oggi",      // Etichetta per la data odierna
    yesterdayLabel: String = "Ieri",  // Etichetta per la data di ieri
    dividersColor: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = LocalTextStyle.current,
    itemHeight: Dp = 40.dp,
    cyclic: Boolean = true,
    customDateFilter: ((LocalDate) -> Boolean)? = null  // Filtro opzionale per date personalizzate
) {
    // Calcola l'intervallo di date disponibili
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    val minDate = today.minusDays(daysBackward.toLong())
    val maxDate = today.plusDays(daysForward.toLong())

    // Genera la lista di date disponibili
    val dateRange = remember(minDate, maxDate, customDateFilter) {
        val dates = mutableListOf<LocalDate>()
        var currentDate = minDate

        while (!currentDate.isAfter(maxDate)) {
            // Applica il filtro personalizzato se presente
            if (customDateFilter == null || customDateFilter(currentDate)) {
                dates.add(currentDate)
            }
            currentDate = currentDate.plusDays(1)
        }

        dates
    }

    // Assicurati che il valore sia nell'intervallo
    val correctedValue = remember(value, dateRange) {
        if (dateRange.contains(value)) {
            value
        } else {
            // Trova la data più vicina nell'intervallo
            dateRange.minByOrNull {
                abs(it.toEpochDay() - value.toEpochDay())
            } ?: today
        }
    }

    // Funzione per formattare le date con etichette speciali
    val formatDate = { date: LocalDate ->
        when {
            date.isEqual(today) -> todayLabel
            date.isEqual(yesterday) -> yesterdayLabel
            else -> date.format(dateFormatter)
        }
    }

    // Utilizza il picker appropriato in base al parametro cyclic
    if (cyclic) {
        CircularListItemPicker(
            modifier = modifier.fillMaxWidth(),
            value = correctedValue,
            list = dateRange,
            onValueChange = onValueChange,
            label = formatDate,
            dividersColor = dividersColor,
            textStyle = textStyle,
            itemHeight = itemHeight
        )
    } else {
        ListItemPicker(
            modifier = modifier.fillMaxWidth(),
            value = correctedValue,
            list = dateRange,
            onValueChange = onValueChange,
            label = formatDate,
            dividersColor = dividersColor,
            textStyle = textStyle,
            itemHeight = itemHeight
        )
    }
}