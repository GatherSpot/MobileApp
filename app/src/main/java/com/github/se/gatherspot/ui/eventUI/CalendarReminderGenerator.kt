package com.github.se.gatherspot.ui.eventUI

import android.content.Intent
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import com.github.se.gatherspot.model.event.Event
import java.util.Calendar

/** Class for generating calendar reminders for events. */
class CalendarReminderGenerator {
  companion object {

    /**
     * Generates a calendar reminder for an event.
     *
     * @param event the event
     * @return the intent for the calendar reminder
     */
    fun generateCalendarReminder(event: Event): Intent {

      val intent =
          Intent(Intent.ACTION_INSERT, Events.CONTENT_URI).apply {
            val beginTime: Calendar =
                Calendar.getInstance().apply {
                  set(
                      event.eventStartDate!!.year,
                      event.eventStartDate.monthValue,
                      event.eventStartDate.dayOfMonth,
                      event.timeBeginning!!.hour,
                      event.timeBeginning.minute)
                }
            val endTime =
                Calendar.getInstance().apply {
                  set(
                      event.eventEndDate!!.year,
                      event.eventEndDate.monthValue,
                      event.eventEndDate.dayOfMonth,
                      event.timeEnding!!.hour,
                      event.timeEnding.minute)
                }
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.timeInMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.timeInMillis)
            putExtra(Events.TITLE, event.title)
            putExtra(Events.EVENT_LOCATION, event.location?.name)
            putExtra(Events.DESCRIPTION, event.description)
          }

      return intent
    }
  }
}
