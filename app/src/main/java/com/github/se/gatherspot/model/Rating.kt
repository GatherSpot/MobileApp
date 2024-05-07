package com.github.se.gatherspot.model


enum class Rating {
    UNRATED,
    ONE_STAR,
    TWO_STARS,
    THREE_STARS,
    FOUR_STARS,
    FIVE_STARS;

    companion object {
        fun fromInt(rating: Int): Rating {
            return when (rating) {
                1 -> ONE_STAR
                2 -> TWO_STARS
                3 -> THREE_STARS
                4 -> FOUR_STARS
                5 -> FIVE_STARS
                else -> UNRATED
            }
        }

        fun toInt(rating: Rating): Int {
            return when (rating) {
                ONE_STAR -> 1
                TWO_STARS -> 2
                THREE_STARS -> 3
                FOUR_STARS -> 4
                FIVE_STARS -> 5
                else -> 0
            }
        }


    }

}