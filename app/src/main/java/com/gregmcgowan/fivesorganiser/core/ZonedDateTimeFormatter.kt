package com.gregmcgowan.fivesorganiser.core

import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class ZonedDateTimeFormatter @Inject constructor() {

   private val datePattern = DateTimeFormatter.ofPattern("EE d MMM yyyy")
   private val timePattern = DateTimeFormatter.ofPattern("HH:mm")

    fun formatDate (zonedTime : ZonedDateTime) : String  {
        return datePattern
                .withLocale(Locale.getDefault())
                .withZone(zonedTime.zone)
                .format(zonedTime)
    }

    fun formatTime (zonedDateTime: ZonedDateTime) : String  {
        return timePattern
                .withLocale(Locale.getDefault())
                .withZone(zonedDateTime.zone)
                .format(zonedDateTime)
    }

}
