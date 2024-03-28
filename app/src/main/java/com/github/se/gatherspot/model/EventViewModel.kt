package com.github.se.gatherspot.model

import java.time.LocalDate
import java.time.LocalTime
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
         * @param eventStartDate: The date of the event
         * @param eventTimeStart: The time the event starts
         * @param eventTimeEnd: The time the event ends
         * @param maxAttendees: The maximum number of attendees
         *
         * @throws Exception if the data is not valid
         */
        fun validateEventData(
            title: String,
            description: String,
            /*location: Location,*/
            eventStartDate: String,
            eventEndDate: String,
            eventTimeStart: String,
            eventTimeEnd: String,
            maxAttendees: String,
            minAttendees: String,
            dateLimitInscription: String,
            timeLimitInscription: String
        ) {
            //test if the date is valid
            val parsedEventDate = try {
                LocalDate.parse(eventStartDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            } catch (e: Exception) {
                throw Exception("Invalid date format")
            }
            //Check whether the start date is in the future
            if (parsedEventDate.isBefore(LocalDate.now())) {
                throw Exception("Event date must be in the future")
            }

            // Check if the end date is valid and after the start date
            val parsedEventEndDate = try {
                LocalDate.parse(eventEndDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            } catch (e: Exception) {
                throw Exception("Invalid end date format")
            }
            if (parsedEventEndDate.isBefore(parsedEventDate)) {
                throw Exception("Event end date must be after start date")
            }


            //Check if eventStartDate is today
            val isToday = parsedEventDate.isEqual(LocalDate.now())

            //test if the time is valid
            val parsedEventTimeStart = try {
                LocalTime.parse(eventTimeStart, DateTimeFormatter.ofPattern("HH:mm"))
            } catch (e: Exception) {
                throw Exception("Invalid time format for start time")
            }
            val parsedEventTimeEnd = try {
                LocalTime.parse(eventTimeEnd, DateTimeFormatter.ofPattern("HH:mm"))
            } catch (e: Exception) {
                throw Exception("Invalid time format for end time")
            }
            //Check if the end time is after the start time
            if (parsedEventTimeEnd.isBefore(parsedEventTimeStart)) {
                throw Exception("Event end time must be after start time")
            }

            //If the event is today, check if the start time is in the future
            if (isToday && parsedEventTimeStart.isBefore(LocalTime.now())) {
                throw Exception("Event start time must be in the future")
            }

            //test if the max attendees is valid
            var parsedMaxAttendees: Int? = null
            if (maxAttendees.isNotEmpty()) {
                parsedMaxAttendees = try {
                    maxAttendees.toInt()
                } catch (e: Exception) {
                    throw Exception("Invalid max attendees format, must be a number")
                }
            }

            //test if the min attendees is valid
            var parsedMinAttendees: Int? = null
            if (minAttendees.isNotEmpty()) {
                parsedMinAttendees = try {
                    minAttendees.toInt()
                } catch (e: Exception) {
                    throw Exception("Invalid min attendees format, must be a number")
                }
                if (parsedMinAttendees!! > parsedMaxAttendees!!) {
                    throw Exception("Minimum attendees must be less than maximum attendees")
                }
            }



            // If given by the user,check if the inscription limit date and time are valid and
            // before the start date and time
            var parsedDateLimitInscription: LocalDate? = null
            var parsedTimeLimitInscription: LocalTime? = null
            if (dateLimitInscription.isNotEmpty()) {

                parsedDateLimitInscription = try {
                    LocalDate.parse(dateLimitInscription, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                } catch (e: Exception) {
                    throw Exception("Invalid inscription limit date format")
                }
                if (parsedDateLimitInscription!!.isAfter(parsedEventDate)) {
                    throw Exception("Inscription limit date must be before event start date")
                }
                //If Limit time is not given, set it to 23:59
                if (timeLimitInscription.isNotEmpty()) {

                    parsedTimeLimitInscription = try {
                        LocalTime.parse(timeLimitInscription, DateTimeFormatter.ofPattern("HH:mm"))
                    } catch (e: Exception) {
                        throw Exception("Invalid inscription limit time format")
                    }
                } else {
                    parsedTimeLimitInscription = LocalTime.of(23, 59)
                }
                if (parsedDateLimitInscription.isEqual(parsedEventDate) && parsedTimeLimitInscription!!.isAfter(
                        parsedEventTimeStart
                    )
                ) {
                    throw Exception("Inscription limit time must be before event start time on the same day")
                }
            }


            //If all the data is valid, create the event
            //createEvent()

        }

        fun addImages() {

        }
    }
}