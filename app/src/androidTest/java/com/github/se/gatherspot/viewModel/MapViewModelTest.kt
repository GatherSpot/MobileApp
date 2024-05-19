package com.github.se.gatherspot.viewModel

import com.github.se.gatherspot.model.MapViewModel
import org.junit.Test

class MapViewModelTest {

  @Test
  fun testConversions() {
    assert(MapViewModel.metersToDegree(0.0, MapViewModel.degreeToMeters(1.0, 0.0)) - 1.0 < 0.0001)
    assert(MapViewModel.metersToDegree(0.0, MapViewModel.degreeToMeters(10.0, 0.0)) - 10.0 < 0.0001)
    assert(MapViewModel.metersToDegree(0.0, MapViewModel.degreeToMeters(90.0, 0.0)) - 90.0 < 0.0001)
    assert(
        MapViewModel.metersToDegree(0.0, MapViewModel.degreeToMeters(180.0, 0.0)) - 180.0 < 0.0001)
  }

  @Test
  fun testConversions2() {
    assert(MapViewModel.metersToDegree(45.0, MapViewModel.degreeToMeters(1.0, 45.0)) - 1.0 < 0.0001)
    assert(
        MapViewModel.metersToDegree(45.0, MapViewModel.degreeToMeters(10.0, 45.0)) - 10.0 < 0.0001)
    assert(
        MapViewModel.metersToDegree(45.0, MapViewModel.degreeToMeters(90.0, 45.0)) - 90.0 < 0.0001)
    assert(
        MapViewModel.metersToDegree(45.0, MapViewModel.degreeToMeters(180.0, 45.0)) - 180.0 <
            0.0001)
  }
}
