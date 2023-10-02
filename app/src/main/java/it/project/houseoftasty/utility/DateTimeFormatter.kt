package it.project.houseoftasty.utility

import android.content.res.Resources
import com.google.firebase.Timestamp
import it.project.houseoftasty.R
import java.time.LocalDateTime

class DateTimeFormatter {
    companion object {

        fun localDateTimeToTemplate(resources: Resources, templateId: Int,  localDateTime: LocalDateTime): String {
            return resources.getString(
                templateId,
                localDateTime.dateToString(true),
                localDateTime.timeToString())
        }

        fun firebaseTimestampToTemplate(resources: Resources, templateId: Int, timestamp: Timestamp): String {
            return localDateTimeToTemplate(resources, templateId, timestamp.toLocalDateTime())
        }

        fun timeDiffToString(timestamp: Timestamp?): String {
            return timePassedToString(timestamp?.timeDiffSeconds() ?: -1)
        }

        fun timePassedToString(secondsPassed: Long): String {
            val minutesPassed: Long = secondsPassed/60
            val hoursPassed: Long = minutesPassed/60
            val daysPassed: Long = hoursPassed/24
            val monthsPassed: Long = daysPassed/30
            val yearsPassed: Long = daysPassed/356

            return when {
                secondsPassed < 60 -> "Adesso"
                minutesPassed == 1L -> "1 minuto fa"
                minutesPassed in 2..59 -> (minutesPassed).toString() + " minuti fa"
                hoursPassed == 1L -> "1 ora fa"
                hoursPassed in 2..23 -> (hoursPassed).toString() + " ore fa"
                daysPassed == 1L -> "1 giorno fa"
                daysPassed in 2..29 -> (daysPassed).toString() + " giorni fa"
                daysPassed in 30..59 -> "1 mese fa"
                daysPassed in 60..355 -> (monthsPassed).toString() + " mesi fa"
                yearsPassed == 1L -> "1 anno fa"
                yearsPassed >= 2 -> (yearsPassed).toString() + " anni fa"
                else -> String()
            }
        }
    }
}