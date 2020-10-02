package com.example.fiorichartcardsapp.utilities

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DataFormatter {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        @JvmStatic
        fun formatMonthForXLabel(date: Int): String {
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val dateParsed = LocalDate.parse(date.toString(), formatter).minusDays(1)
            val xLabelFormatter = DateTimeFormatter.ofPattern("MMM")
            return dateParsed.format(xLabelFormatter)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        @JvmStatic
        fun formatDateForXLabel(date: Int): String {
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val dateParsed = LocalDate.parse(date.toString(), formatter)
            val xLabelFormatter = DateTimeFormatter.ofPattern("MM/dd")
            return dateParsed.format(xLabelFormatter)
        }
    }
}