package nl.menio.moneybunqer.model

import java.io.Serializable
import java.util.*

data class Period(
        val start: Date,
        val end: Date
) : Serializable