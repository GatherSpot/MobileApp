package com.github.se.gatherspot.model

import kotlinx.coroutines.test.runTest
import org.junit.Test

class MapViewModelTest {


    @Test
    fun testConversions() {
        assert (MapViewModel.metersToDegree(0.0,MapViewModel.degreeToMeters(1.0)) - 1.0 < 0.0001)
        assert (MapViewModel.metersToDegree(0.0,MapViewModel.degreeToMeters(10.0)) - 10.0 < 0.0001)
        assert (MapViewModel.metersToDegree(0.0,MapViewModel.degreeToMeters(90.0)) - 90.0 < 0.0001)
        assert (MapViewModel.metersToDegree(0.0,MapViewModel.degreeToMeters(180.0)) - 180.0 < 0.0001)
    }

    @Test
    fun testConversions2() {
        assert (MapViewModel.metersToDegree(45.0,MapViewModel.degreeToMeters(1.0)) - 1.0 < 0.001)
        assert (MapViewModel.metersToDegree(45.0,MapViewModel.degreeToMeters(10.0)) - 10.0 < 0.001)
        assert (MapViewModel.metersToDegree(45.0,MapViewModel.degreeToMeters(90.0)) - 90.0 < 0.001)
        assert (MapViewModel.metersToDegree(45.0,MapViewModel.degreeToMeters(180.0)) - 180.0 < 0.001)
    }


}