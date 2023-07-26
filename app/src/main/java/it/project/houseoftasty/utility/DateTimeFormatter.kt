package it.project.houseoftasty.utility

import android.content.res.Resources
import com.google.firebase.Timestamp
import it.project.houseoftasty.R
import java.time.LocalDateTime

class DateTimeFormatter {
    companion object {

        fun localDateTimeToTemplate(resources: Resources, localDateTime: LocalDateTime): String {
            return resources.getString(
                R.string.timestampTemplate,
                localDateTime.dateToString(true),
                localDateTime.timeToString())
        }

        fun firebaseTimestampToTemplate(resources: Resources, timestamp: Timestamp): String {
            return localDateTimeToTemplate(resources, timestamp.toLocalDateTime())
        }

    }
}