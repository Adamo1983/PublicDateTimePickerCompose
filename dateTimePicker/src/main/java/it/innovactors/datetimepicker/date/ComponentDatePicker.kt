package it.innovactors.datetimepicker.date

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import it.innovactors.datetimepicker.commonComponents.CircularListItemPicker
import it.innovactors.datetimepicker.commonComponents.ListItemPicker
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import kotlin.collections.toList

/**
 * Un DatePicker che consente di selezionare separatamente giorno, mese e anno
 * con eventuali limiti di date minima e massima.
 */
@Composable
fun ComponentDatePicker(
    modifier: Modifier = Modifier,
    value: LocalDate,
    onValueChange: (LocalDate) -> Unit,
    minDate: LocalDate? = null,  // Data minima selezionabile (null = nessun limite)
    maxDate: LocalDate? = null,  // Data massima selezionabile (null = nessun limite)
    monthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM"),
    dayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d"),
    yearFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy"),
    dayMonthDivider: (@Composable () -> Unit)? = null,
    monthYearDivider: (@Composable () -> Unit)? = null,
    showYear: Boolean = true,
    dividersColor: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = LocalTextStyle.current,
    itemHeight: Dp = 40.dp,
    cyclic: Boolean = true
) {
    // Definisci limiti ragionevoli se non specificati
    val effectiveMinDate = remember(minDate) {
        minDate ?: LocalDate.of(1900, 1, 1)  // Se non specificato, usa 1900 come anno minimo
    }

    val effectiveMaxDate = remember(maxDate) {
        maxDate ?: LocalDate.of(2100, 12, 31)  // Se non specificato, usa 2100 come anno massimo
    }

    // Correggi la data se necessario
    val correctedValue = remember(value, effectiveMinDate, effectiveMaxDate) {
        when {
            value.isBefore(effectiveMinDate) -> effectiveMinDate
            value.isAfter(effectiveMaxDate) -> effectiveMaxDate
            else -> value
        }
    }

    // Determina gli anni disponibili
    val availableYears = remember(effectiveMinDate, effectiveMaxDate) {
        (effectiveMinDate.year..effectiveMaxDate.year).toList()
    }

    // Determina i mesi disponibili per l'anno corrente
    val availableMonthsInYear = remember(correctedValue.year, effectiveMinDate, effectiveMaxDate) {
        val firstMonthValue = when (correctedValue.year) {
            effectiveMinDate.year -> effectiveMinDate.monthValue
            else -> 1
        }

        val lastMonthValue = when (correctedValue.year) {
            effectiveMaxDate.year -> effectiveMaxDate.monthValue
            else -> 12
        }

        (firstMonthValue..lastMonthValue).toList()
    }

    // Determina i giorni disponibili per il mese e l'anno correnti
    val availableDaysInMonth = remember(correctedValue.year, correctedValue.month, effectiveMinDate, effectiveMaxDate) {
        val firstDay = when {
            correctedValue.year == effectiveMinDate.year &&
                    correctedValue.month == effectiveMinDate.month -> effectiveMinDate.dayOfMonth
            else -> 1
        }

        val month = correctedValue.month
        val year = correctedValue.year
        val lastDayOfMonth = month.length(year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))

        val lastDay = when {
            correctedValue.year == effectiveMaxDate.year &&
                    correctedValue.month == effectiveMaxDate.month -> minOf(lastDayOfMonth, effectiveMaxDate.dayOfMonth)
            else -> lastDayOfMonth
        }

        (firstDay..lastDay).toList()
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Selettore del giorno
        if (cyclic && availableDaysInMonth.size > 3) {
            CircularListItemPicker(
                modifier = Modifier.weight(1f),
                value = correctedValue.dayOfMonth,
                list = availableDaysInMonth,
                onValueChange = { day ->
                    val newDate = try {
                        LocalDate.of(correctedValue.year, correctedValue.month, day)
                    } catch (e: Exception) {
                        // Se il giorno non è valido per questo mese, usa l'ultimo giorno del mese
                        val lastDayOfMonth = correctedValue.month
                            .length(correctedValue.year % 4 == 0 && (correctedValue.year % 100 != 0 || correctedValue.year % 400 == 0))
                        LocalDate.of(correctedValue.year, correctedValue.month, minOf(day, lastDayOfMonth))
                    }

                    // Verifica che la nuova data sia nell'intervallo consentito
                    if (!(minDate != null && newDate.isBefore(minDate)) &&
                        !(maxDate != null && newDate.isAfter(maxDate))) {
                        onValueChange(newDate)
                    }
                },
                label = { day -> LocalDate.of(correctedValue.year, correctedValue.month, day).format(dayFormatter) },
                dividersColor = dividersColor,
                textStyle = textStyle,
                itemHeight = itemHeight
            )
        } else {
            ListItemPicker(
                modifier = Modifier.weight(1f),
                value = correctedValue.dayOfMonth,
                list = availableDaysInMonth,
                onValueChange = { day ->
                    val newDate = try {
                        LocalDate.of(correctedValue.year, correctedValue.month, day)
                    } catch (e: Exception) {
                        // Se il giorno non è valido per questo mese, usa l'ultimo giorno del mese
                        val lastDayOfMonth = correctedValue.month
                            .length(correctedValue.year % 4 == 0 && (correctedValue.year % 100 != 0 || correctedValue.year % 400 == 0))
                        LocalDate.of(correctedValue.year, correctedValue.month, minOf(day, lastDayOfMonth))
                    }

                    // Verifica che la nuova data sia nell'intervallo consentito
                    if (!(minDate != null && newDate.isBefore(minDate)) &&
                        !(maxDate != null && newDate.isAfter(maxDate))) {
                        onValueChange(newDate)
                    }
                },
                label = { day -> LocalDate.of(correctedValue.year, correctedValue.month, day).format(dayFormatter) },
                dividersColor = dividersColor,
                textStyle = textStyle,
                itemHeight = itemHeight
            )
        }

        dayMonthDivider?.invoke() ?: Text(
            text = "/",
            modifier = Modifier.padding(horizontal = 4.dp),
            style = textStyle
        )

        // Selettore del mese
        if (cyclic && availableMonthsInYear.size > 3) {
            CircularListItemPicker(
                modifier = Modifier.weight(1.5f),
                value = correctedValue.monthValue,
                list = availableMonthsInYear,
                onValueChange = { monthValue ->
                    // Calcola l'ultimo giorno valido per il nuovo mese
                    val month = Month.of(monthValue)
                    val lastDayOfMonth = month.length(correctedValue.year % 4 == 0 &&
                            (correctedValue.year % 100 != 0 || correctedValue.year % 400 == 0))

                    // Adatta il giorno se necessario per il nuovo mese
                    val adjustedDay = minOf(correctedValue.dayOfMonth, lastDayOfMonth)

                    val newDate = try {
                        LocalDate.of(correctedValue.year, monthValue, adjustedDay)
                    } catch (e: Exception) {
                        // Fallback a una data valida
                        LocalDate.of(correctedValue.year, monthValue, 1)
                    }

                    // Verifica che la nuova data sia nell'intervallo consentito
                    if (!(minDate != null && newDate.isBefore(minDate)) &&
                        !(maxDate != null && newDate.isAfter(maxDate))) {
                        onValueChange(newDate)
                    }
                },
                label = { monthValue ->
                    LocalDate.of(correctedValue.year, monthValue, 1).format(monthFormatter)
                },
                dividersColor = dividersColor,
                textStyle = textStyle,
                itemHeight = itemHeight
            )
        } else {
            ListItemPicker(
                modifier = Modifier.weight(1.5f),
                value = correctedValue.monthValue,
                list = availableMonthsInYear,
                onValueChange = { monthValue ->
                    // Calcola l'ultimo giorno valido per il nuovo mese
                    val month = Month.of(monthValue)
                    val lastDayOfMonth = month.length(correctedValue.year % 4 == 0 &&
                            (correctedValue.year % 100 != 0 || correctedValue.year % 400 == 0))

                    // Adatta il giorno se necessario per il nuovo mese
                    val adjustedDay = minOf(correctedValue.dayOfMonth, lastDayOfMonth)

                    val newDate = try {
                        LocalDate.of(correctedValue.year, monthValue, adjustedDay)
                    } catch (e: Exception) {
                        // Fallback a una data valida
                        LocalDate.of(correctedValue.year, monthValue, 1)
                    }

                    // Verifica che la nuova data sia nell'intervallo consentito
                    if (!(minDate != null && newDate.isBefore(minDate)) &&
                        !(maxDate != null && newDate.isAfter(maxDate))) {
                        onValueChange(newDate)
                    }
                },
                label = { monthValue ->
                    LocalDate.of(correctedValue.year, monthValue, 1).format(monthFormatter)
                },
                dividersColor = dividersColor,
                textStyle = textStyle,
                itemHeight = itemHeight
            )
        }

        if (showYear) {
            monthYearDivider?.invoke() ?: Text(
                text = "/",
                modifier = Modifier.padding(horizontal = 4.dp),
                style = textStyle
            )

            // Selettore dell'anno (usa cyclic solo se c'è un range significativo di anni)
            if (cyclic && availableYears.size > 10) {
                CircularListItemPicker(
                    modifier = Modifier.weight(1.5f),
                    value = correctedValue.year,
                    list = availableYears,
                    onValueChange = { year ->
                        // Per l'anno, dobbiamo gestire anche cambiamenti nei mesi disponibili
                        var month = correctedValue.monthValue

                        // Verifica se il mese corrente è valido nel nuovo anno
                        val firstMonth = if (year == effectiveMinDate.year) effectiveMinDate.monthValue else 1
                        val lastMonth = if (year == effectiveMaxDate.year) effectiveMaxDate.monthValue else 12

                        // Adatta il mese se necessario
                        if (month < firstMonth) month = firstMonth
                        if (month > lastMonth) month = lastMonth

                        // Calcola l'ultimo giorno valido per il nuovo mese/anno
                        val lastDayOfMonth = Month.of(month)
                            .length(year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))

                        // Adatta il giorno se necessario
                        var day = correctedValue.dayOfMonth
                        if (day > lastDayOfMonth) day = lastDayOfMonth

                        // Ulteriori aggiustamenti se siamo ai limiti dell'intervallo
                        if (year == effectiveMinDate.year && month == effectiveMinDate.monthValue &&
                            day < effectiveMinDate.dayOfMonth) {
                            day = effectiveMinDate.dayOfMonth
                        } else if (year == effectiveMaxDate.year && month == effectiveMaxDate.monthValue &&
                            day > effectiveMaxDate.dayOfMonth) {
                            day = effectiveMaxDate.dayOfMonth
                        }

                        val newDate = try {
                            LocalDate.of(year, month, day)
                        } catch (e: Exception) {
                            // Fallback a una data valida
                            if (year <= effectiveMinDate.year) effectiveMinDate else effectiveMaxDate
                        }

                        // Verifica che la nuova data sia nell'intervallo consentito
                        if (!(minDate != null && newDate.isBefore(minDate)) &&
                            !(maxDate != null && newDate.isAfter(maxDate))) {
                            onValueChange(newDate)
                        }
                    },
                    label = { year -> year.toString().format(yearFormatter) },
                    dividersColor = dividersColor,
                    textStyle = textStyle,
                    itemHeight = itemHeight
                )
            } else {
                ListItemPicker(
                    modifier = Modifier.weight(1.5f),
                    value = correctedValue.year,
                    list = availableYears,
                    onValueChange = { year ->
                        // Per l'anno, dobbiamo gestire anche cambiamenti nei mesi disponibili
                        var month = correctedValue.monthValue

                        // Verifica se il mese corrente è valido nel nuovo anno
                        val firstMonth = if (year == effectiveMinDate.year) effectiveMinDate.monthValue else 1
                        val lastMonth = if (year == effectiveMaxDate.year) effectiveMaxDate.monthValue else 12

                        // Adatta il mese se necessario
                        if (month < firstMonth) month = firstMonth
                        if (month > lastMonth) month = lastMonth

                        // Calcola l'ultimo giorno valido per il nuovo mese/anno
                        val lastDayOfMonth = Month.of(month)
                            .length(year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))

                        // Adatta il giorno se necessario
                        var day = correctedValue.dayOfMonth
                        if (day > lastDayOfMonth) day = lastDayOfMonth

                        // Ulteriori aggiustamenti se siamo ai limiti dell'intervallo
                        if (year == effectiveMinDate.year && month == effectiveMinDate.monthValue &&
                            day < effectiveMinDate.dayOfMonth) {
                            day = effectiveMinDate.dayOfMonth
                        } else if (year == effectiveMaxDate.year && month == effectiveMaxDate.monthValue &&
                            day > effectiveMaxDate.dayOfMonth) {
                            day = effectiveMaxDate.dayOfMonth
                        }

                        val newDate = try {
                            LocalDate.of(year, month, day)
                        } catch (e: Exception) {
                            // Fallback a una data valida
                            if (year <= effectiveMinDate.year) effectiveMinDate else effectiveMaxDate
                        }

                        // Verifica che la nuova data sia nell'intervallo consentito
                        if (!(minDate != null && newDate.isBefore(minDate)) &&
                            !(maxDate != null && newDate.isAfter(maxDate))) {
                            onValueChange(newDate)
                        }
                    },
                    label = { year -> year.toString().format(yearFormatter) },
                    dividersColor = dividersColor,
                    textStyle = textStyle,
                    itemHeight = itemHeight
                )
            }
        }
    }
}