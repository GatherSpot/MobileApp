package com.github.se.gatherspot.model.event

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.location.Location

/**
 * Data class for a draft event. Every field (except for the location, the image and the categories)
 * is saved as a string.
 */
data class DraftEvent(
    val organiserId: String,
    val title: String?,
    val description: String?,
    val location: Location?,
    val eventStartDate: String?,
    val eventEndDate: String?,
    val timeBeginning: String?,
    val timeEnding: String?,
    val attendanceMaxCapacity: String?,
    val attendanceMinCapacity: String?,
    val inscriptionLimitDate: String?,
    val inscriptionLimitTime: String?,
    val categories: Set<Interests>? = emptySet(),
    val images: ImageBitmap? = ImageBitmap(30, 30, config = ImageBitmapConfig.Rgb565)
)
