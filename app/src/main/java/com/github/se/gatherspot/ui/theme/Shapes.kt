package com.github.se.gatherspot.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

fun drawPill(drawScope: DrawScope, topLeft: Offset, size: Size, color: Color) {
  val horizontalStretch = 1.000f
  val verticalStretch = 1.1f
  drawScope.drawRect(
      topLeft =
          topLeft +
              Offset(
                  -size.width * (0.5f * horizontalStretch - 0.5f),
                  -size.height * (0.5f * verticalStretch - 0.5f)),
      color = color,
      size = Size(width = size.width * horizontalStretch, size.height * verticalStretch))

  drawScope.drawCircle(
      color = color,
      center = topLeft + Offset(size.width * (0.5f + 0.5f * horizontalStretch), 0.5f * size.height),
      radius = 0.5f * size.height * verticalStretch,
  )

  drawScope.drawCircle(
      color = color,
      center = topLeft + Offset((0.5f - 0.5f * horizontalStretch) * size.width, 0.5f * size.height),
      radius = 0.5f * size.height * verticalStretch,
  )
}
