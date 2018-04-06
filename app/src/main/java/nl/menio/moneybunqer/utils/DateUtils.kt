package nl.menio.moneybunqer.utils

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
    }
}