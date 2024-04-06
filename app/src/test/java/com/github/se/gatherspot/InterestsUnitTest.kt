package com.github.se.gatherspot

import com.github.se.gatherspot.model.Interests
import org.junit.Assert.*
import org.junit.Test
import java.util.BitSet


class InterestsUnitTest {

    @Test
    fun newBitset_isCorrect() {
        val bitSet = BitSet(Interests.entries.size)
        bitSet.clear()
        assertEquals(bitSet, Interests.newBitset())

    }
    @Test
    fun hasInterests_isCorrect() {
        val bitSet = BitSet(Interests.entries.size)
        bitSet.clear()
        bitSet.set(0)
        bitSet.set(3)
        bitSet.set(4)
        bitSet.set(6)
        bitSet.set(8)

        // bitset : 1001101010000
        assertTrue(Interests.hasInterest(bitSet, Interests.entries[0]))
        assertFalse(Interests.hasInterest(bitSet, Interests.entries[1]))
        assertFalse(Interests.hasInterest(bitSet, Interests.entries[2]))
        assertTrue(Interests.hasInterest(bitSet, Interests.entries[3]))
        assertTrue(Interests.hasInterest(bitSet, Interests.entries[4]))
        assertFalse(Interests.hasInterest(bitSet, Interests.entries[5]))
        assertTrue(Interests.hasInterest(bitSet, Interests.entries[6]))
        assertFalse(Interests.hasInterest(bitSet, Interests.entries[7]))
        assertTrue(Interests.hasInterest(bitSet, Interests.entries[8]))
        assertFalse(Interests.hasInterest(bitSet, Interests.entries[9]))
        assertFalse(Interests.hasInterest(bitSet, Interests.entries[10]))


    }

    @Test
    fun addInterest_isCorrect() {
        val bitSet = Interests.newBitset()

        Interests.addInterest(bitSet, Interests.entries[0])
        Interests.addInterest(bitSet, Interests.entries[3])
        Interests.addInterest(bitSet, Interests.entries[4])
        Interests.addInterest(bitSet, Interests.entries[6])
        Interests.addInterest(bitSet, Interests.entries[8])

        assertTrue(bitSet[0])
        assertFalse(bitSet[1])
        assertFalse(bitSet[2])
        assertTrue(bitSet[3])
        assertTrue(bitSet[4])
        assertFalse(bitSet[5])
        assertTrue(bitSet[6])
        assertFalse(bitSet[7])
        assertTrue(bitSet[8])
    }

    @Test
    fun removeInterest_isCorrect() {
        val bitSet = Interests.newBitset()
        bitSet.set(0, Interests.entries.size-1)

        Interests.removeInterest(bitSet, Interests.entries[0])
        Interests.removeInterest(bitSet, Interests.entries[3])
        Interests.removeInterest(bitSet, Interests.entries[4])
        Interests.removeInterest(bitSet, Interests.entries[6])
        Interests.removeInterest(bitSet, Interests.entries[8])

        assertFalse(bitSet[0])
        assertTrue(bitSet[1])
        assertTrue(bitSet[2])
        assertFalse(bitSet[3])
        assertFalse(bitSet[4])
        assertTrue(bitSet[5])
        assertFalse(bitSet[6])
        assertTrue(bitSet[7])
        assertFalse(bitSet[8])
    }

    @Test
    fun listInterests_isCorrect(){
        val bitSet = BitSet(Interests.entries.size)
        bitSet.clear()
        bitSet.set(0)
        bitSet.set(3)
        bitSet.set(4)
        bitSet.set(6)
        bitSet.set(8)

        val list = Interests.listInterests(bitSet)
        assertEquals(5, list.size)
        assertTrue(list.contains(Interests.entries[0]))
        assertTrue(list.contains(Interests.entries[3]))
        assertTrue(list.contains(Interests.entries[4]))
        assertTrue(list.contains(Interests.entries[6]))
        assertTrue(list.contains(Interests.entries[8]))
    }

    @Test
    fun addParentInterest_isCorrect(){
        var bitSet = Interests.newBitset()
        Interests.addParentInterest(bitSet, Interests.BASKETBALL)
        assertTrue(bitSet[Interests.SPORT.ordinal])

        bitSet = Interests.newBitset()
        Interests.addParentInterest(bitSet, Interests.FOOTBALL)
        assertTrue(bitSet[Interests.SPORT.ordinal])

        bitSet = Interests.newBitset()
        Interests.addParentInterest(bitSet, Interests.TENNIS)
        assertTrue(bitSet[Interests.SPORT.ordinal])

        bitSet = Interests.newBitset()
        Interests.addParentInterest(bitSet, Interests.BOWLING)
        assertTrue(bitSet[Interests.LEISURE.ordinal])


    }

    @Test
    fun addChildrenInterest_isCorrect(){
        var bitSet = Interests.newBitset()
        Interests.addChildrenInterest(bitSet, Interests.SPORT)
        assertTrue(bitSet[Interests.BASKETBALL.ordinal])
        assertTrue(bitSet[Interests.FOOTBALL.ordinal])
        assertTrue(bitSet[Interests.TENNIS.ordinal])
        assertFalse(bitSet[Interests.BOWLING.ordinal])

        bitSet = Interests.newBitset()
        Interests.addChildrenInterest(bitSet, Interests.LEISURE)
        assertFalse(bitSet[Interests.BASKETBALL.ordinal])
        assertFalse(bitSet[Interests.FOOTBALL.ordinal])
        assertFalse(bitSet[Interests.TENNIS.ordinal])
        assertTrue(bitSet[Interests.BOWLING.ordinal])

    }

    @Test
    fun removeChildrenInterest_isCorrect(){
        var bitSet = Interests.newBitset()
        bitSet.set(0,Interests.entries.size)
        Interests.removeChildrenInterest(bitSet, Interests.SPORT)
        assertFalse(bitSet[Interests.BASKETBALL.ordinal])
        assertFalse(bitSet[Interests.FOOTBALL.ordinal])
        assertFalse(bitSet[Interests.TENNIS.ordinal])
        assertTrue(bitSet[Interests.BOWLING.ordinal])

        bitSet = Interests.newBitset()
        bitSet.set(0, Interests.entries.size)
        Interests.removeChildrenInterest(bitSet, Interests.LEISURE)
        assertTrue(bitSet[Interests.BASKETBALL.ordinal])
        assertTrue(bitSet[Interests.FOOTBALL.ordinal])
        assertTrue(bitSet[Interests.TENNIS.ordinal])
        assertFalse(bitSet[Interests.BOWLING.ordinal])

    }








}

