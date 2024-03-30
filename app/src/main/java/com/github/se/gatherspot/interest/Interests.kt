package com.github.se.gatherspot.interest

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.se.gatherspot.ui.theme.Pink40
import com.google.maps.android.compose.Circle
import java.util.BitSet


enum class Interests {
    SPORT ,
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
    TRAVEL,
    LEISURE,
    BOWLING
    ;


    // This companion object contains utility functions for working with Interests
    companion object {



        val parents = mapOf(
            FOOTBALL to SPORT,
            BASKETBALL to SPORT,
            TENNIS to SPORT,
            BOWLING to LEISURE
        )

        val children = parents.toList().groupBy({it.second}, {it.first})


        fun color(interest: Interests) : Color{
            return Pink40;
        }

        // This function creates a new BitSet to store interests
        fun newBitset(): BitSet{
            return BitSet(Interests.entries.size)
        }

        // This function checks if a particular interest is present in the BitSet
        fun hasInterest(bitset: BitSet, interest: Interests): Boolean{
            return bitset.get(interest.ordinal)
        }

        // This function adds a particular interest to the BitSet without adding parent interest
        fun addInterest(bitset: BitSet, interest: Interests){
            bitset.set(interest.ordinal)
        }


        // This function adds recursively the parent interest of a particular interest to the BitSet
        // It also adds the interest itself
        // This function is used at event creation
        fun addParentInterest(bitset: BitSet, interest: Interests){
            val parent = parents[interest]
            if (parent != null){
                addParentInterest(bitset, parent)
                addInterest(bitset, parent)
            }
        }

        /*
        This function adds recursively the child interest of a particular interest to the BitSet
        It also adds the interest itself
        This function might be used during interest selection by a user or an event creator
        User side : Select sport -> every sport or only specific subset ? With this function highlight
        all subcategories so they can adjust their selection
        Event side : Select sport -> is it a specific sport ? With this function offer more specific
        and appropriate tags  to compliment
         */
        fun addChildrenInterest(bitset: BitSet, interest : Interests){
            val children = children[interest]
            if (children != null){
                children.forEach { child ->
                    addChildrenInterest(bitset, child)
                    addInterest(bitset, child)
                }
            }
        }
        



        @Composable
        fun selectUserInterestsScreen(selection: MutableState<BitSet>, paddingValues: PaddingValues){
            // This function is used to select interests of a user or event

            for (interest in Interests.values()){
                Text(
                    modifier = Modifier
                        .padding(16.dp)
                        .drawBehind {
                            drawCircle(
                                color = color(interest),
                                radius = this.size.width
                            )
                        },
                    text = Interests.CHESS.toString(),
                )

            }


        }


    }

}



