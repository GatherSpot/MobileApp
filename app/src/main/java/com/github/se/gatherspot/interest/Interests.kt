package com.github.se.gatherspot.interest

import java.util.BitSet


enum class Interests {
    SPORT,
    FOOTBALL,
    BASKETBALL,
    TENNIS,
    BOARD_GAMES,
    CHESS,
    ROLE_PLAY,
    VIDEO_GAMES,
    NIGHTLIFE,
    CONCERTS,
    TECHNOLOGY,
    NETWORKING,
    SPEED_DATING,
    ART,
    TRAVEL;
    companion object {

        val parent = mapOf(
            FOOTBALL to setOf(SPORT),
            BASKETBALL to setOf(SPORT),
            TENNIS to setOf(SPORT),
        )
        fun newBitset(): BitSet{
            return BitSet(Interests.entries.size)
        }

        fun hasInterest(bitset: BitSet, interest: Interests): Boolean{
            return bitset.get(interest.ordinal)
        }

        fun addInterest(bitset: BitSet, interest: Interests){
            bitset.set(interest.ordinal)
        }

        fun removeInterest(bitset: BitSet, interest: Interests){
            bitset.clear(interest.ordinal)
        }

        fun addParentInterest(bitset: BitSet, interest: Interests){
            val children = parent[interest]
            if (children != null){
                for (child in children){
                    bitset.set(child.ordinal)
                }
            }
        }

    }

}



