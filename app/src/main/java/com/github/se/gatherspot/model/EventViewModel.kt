package com.github.se.gatherspot.model

import com.github.se.gatherspot.model.location.Location
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EventViewModel {
    companion object {
        //From verified inputs create an event
        fun createEvent() {
            //firebase.getNewUID()

        }

        /**
         * Check if the data entered by the user is valid
         * Parse the data and check if it is in the correct format, then call createEvent function
         *
         * @param title: The title of the event
         * @param description: A short description of the event
         * @param eventDate: The date of the event
         * @param eventTimeStart: The time the event starts
         * @param eventTimeEnd: The time the event ends
         * @param maxAttendees: The maximum number of attendees
         *
         * @throws Exception if the data is not valid
         */
        fun validateEventData(
            title: String,
            description: String,
            location: Location,
            eventDate: String,
            eventTimeStart: String,
            eventTimeEnd: String,
            maxAttendees: String,
        ) {
            //test if the date is valid
            val parsedEventDate = try {
                LocalDate.parse(eventDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            } catch (e: Exception) {
                throw Exception("Invalid date format")
            }
            //Check whether the date is in the future
            if (parsedEventDate.isBefore(LocalDate.now())) {
                throw Exception("Event date must be in the future")
            }

            //Check if eventDate is today
            val isToday = parsedEventDate.isEqual(LocalDate.now())

            //test if the time is valid
            val parsedEventTimeStart = try {
                LocalDate.parse(eventTimeStart, DateTimeFormatter.ofPattern("hh:mm"))
            } catch (e: Exception) {
                throw Exception("Invalid time format for start time")
            }
            val parsedEventTimeEnd = try {
                LocalDate.parse(eventTimeEnd, DateTimeFormatter.ofPattern("hh:mm"))
            } catch (e: Exception) {
                throw Exception("Invalid time format for end time")
            }
            //Check if the end time is after the start time
            if (parsedEventTimeEnd.isBefore(parsedEventTimeStart)) {
                throw Exception("Event end time must be after start time")
            }

            //If the event is today, check if the start time is in the future
            if (isToday && parsedEventTimeStart.isBefore(LocalDate.now())) {
                throw Exception("Event start time must be in the future")
            }


            //test if the max attendees is valid
            var parsedMaxAttendees: Int? = null
            if (!maxAttendees.isEmpty()) {
                parsedMaxAttendees = try {
                    maxAttendees.toInt()
                } catch (e: Exception) {
                    throw Exception("Invalid max attendees format, must be a number")
                }
            }

            //If all the data is valid, create the event
            //createEvent()

        }

        fun addImages() {

        }
    }
}