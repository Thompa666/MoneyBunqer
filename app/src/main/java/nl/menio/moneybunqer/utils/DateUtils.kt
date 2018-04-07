package nl.menio.moneybunqer.utils

import nl.menio.moneybunqer.model.Period
import java.text.SimpleDateFormat
import java.util.*

class DateUtils() {

    companion object {
        val TAG: String = DateUtils::class.java.simpleName
        const val BUNQ_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S"

        fun parse(dateString: String) : Date {
            val formatter = SimpleDateFormat(BUNQ_DATE_FORMAT, Locale.US)
            return formatter.parse(dateString)
        }

        fun yesterday() : Period {
            val now = Calendar.getInstance()

            // Get the allowed start date
            val start = Calendar.getInstance()
            start.time = now.time
            start.add(Calendar.DAY_OF_MONTH, -1)
            start.set(Calendar.HOUR_OF_DAY, 0)
            start.set(Calendar.MINUTE, 0)
            start.set(Calendar.SECOND, 0)
            start.set(Calendar.MILLISECOND, 0)

            // Get the allowed end date
            val end = Calendar.getInstance()
            end.time = start.time
            end.add(Calendar.DAY_OF_MONTH, 1)
            end.add(Calendar.MILLISECOND, -1)

            // Return both dates in a period object
            return Period(start.time, end.time)
        }

        fun today() : Period {
            val now = Calendar.getInstance()

            // Get the allowed start date
            val start = Calendar.getInstance()
            start.time = now.time
            start.set(Calendar.HOUR_OF_DAY, 0)
            start.set(Calendar.MINUTE, 0)
            start.set(Calendar.SECOND, 0)
            start.set(Calendar.MILLISECOND, 0)

            // Get the allowed end date
            val end = Calendar.getInstance()
            end.time = start.time
            end.add(Calendar.DAY_OF_MONTH, 1)
            end.add(Calendar.MILLISECOND, -1)

            // Return both dates in a period object
            return Period(start.time, end.time)
        }
    }
}