package com.github.se.gatherspot.ui.event

import android.content.Intent
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import com.github.se.gatherspot.model.event.Event
import java.util.Calendar

class CalendarReminderGenerator {
    companion object {
        fun generateCalendarReminder(event : Event) {

            Intent(Intent.ACTION_INSERT, Events.CONTENT_URI).apply {
                val beginTime: Calendar = Calendar.getInstance().apply {
                    set(
                        event.eventStartDate!!.year,
                        event.eventStartDate!!.monthValue,
                        event.eventStartDate!!.dayOfMonth,
                        event.timeBeginning!!.hour,
                        event.timeBeginning!!.minute
                    )
                }
                val endTime = Calendar.getInstance().apply {
                    set(
                        event.eventEndDate!!.year,
                        event.eventEndDate!!.monthValue,
                        event.eventEndDate!!.dayOfMonth,
                        event.timeEnding!!.hour,
                        event.timeEnding!!.minute
                    )
                }
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.timeInMillis)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.timeInMillis)
                putExtra(Events.TITLE, event.title)
                putExtra(Events.EVENT_LOCATION, event.location?.name)
                putExtra(Events.DESCRIPTION, event.description)

            }

            startActivity(intent)

            /*
            val intent = Intent(Intent.ACTION_INSERT, Events.CONTENT_URI)
            intent.setData(android.provider.CalendarContract.Events.CONTENT_URI)
            intent.putExtra(CalendarContract.Events.TITLE, event.title)
            intent.putExtra(CalendarContract.Events.DESCRIPTION, event.description)
            val startTime : Calendar = Calendar.getInstance()
            startTime.clear()
            event.eventStartDate?.let {
                startTime.set(it.year, it.monthValue, it.dayOfMonth)
            }
            event.timeBeginning?.let {
                startTime.set(Calendar.HOUR_OF_DAY, it.hour)
                startTime.set(Calendar.MINUTE, it.minute)
            }
            startActivity( intent)

             */


        }
    }
}