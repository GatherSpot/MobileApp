package com.github.se.gatherspot.interest

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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

        val unselected : Color = Color(0xFFFFFFFF)

        val parents = mapOf(
            FOOTBALL to SPORT,
            BASKETBALL to SPORT,
            TENNIS to SPORT,
            BOWLING to LEISURE
        )

        val children = parents.toList().groupBy({it.second}, {it.first})


        fun color(selection: BitSet, interest: Interests) : Color{
            if (hasInterest(selection, interest)){
                return Pink40
            }
            return unselected;
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

        // This function removes a particular interest to the BitSet without adding parent interest
        fun removeInterest(bitset: BitSet, interest: Interests){
            bitset.clear(interest.ordinal)
        }


        // This function adds recursively the parent interest of a particular interest to the BitSet
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

        fun flip(bitset: BitSet, interest: Interests){
            if (hasInterest(bitset, interest)){
                removeChildrenInterest(bitset, interest)
                removeInterest(bitset,interest)
            } else {
                addChildrenInterest(bitset,interest)
                addInterest(bitset,interest)
            }
        }

        fun removeChildrenInterest(bitset: BitSet, interest: Interests){
            val children = children[interest]
            if (children != null){
                children.forEach { child ->
                    removeChildrenInterest(bitset, child)
                    removeInterest(bitset, child)
                }
            }
        }



        @Composable
        fun Display(selection: MutableState<BitSet>, interest: Interests, paddingValues: PaddingValues){
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable(onClick = {
                        flip(selection.value, interest)
                    })
                    .drawBehind {

                        drawRect(
                            topLeft = Offset(-this.size.width*0.1f,-this.size.height*0.1f),
                            color = color(selection.value, interest),
                            size = Size(width = this.size.width * 1.2f , this.size.height * 1.2f)
                        )
                    },
                text = interest.toString(),
            )
        }
        @Composable
        fun selectUserInterestsScreen(selection: MutableState<BitSet>, paddingValues: PaddingValues){
            // This function is used to select interests of a user or event
            Column {
                for (i in 0..entries.size-3 step 3) {
                    Row{
                        Display(selection, entries.get(i), paddingValues)
                        Display(selection, entries.get(i+1), paddingValues)
                        Display(selection, entries.get(i+2), paddingValues)

                    }

                }
                Row {
                    for (i in entries.size-2..entries.size-1){
                        Display(selection, entries.get(i), paddingValues)
                    }
                }
            }


        }


    }

}



