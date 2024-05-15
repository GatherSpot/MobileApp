package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class QRCodeScannerScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<QRCodeScannerScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("QRCodeScanner") }){
    val scaffold: KNode = onNode { hasTestTag("QRCodeScanner") }
    val cameraPermissionButton: KNode = onNode { hasTestTag("cameraPermissionButton") }
    val cameraPreview = onNode { hasTestTag("CameraPreview") }
    }