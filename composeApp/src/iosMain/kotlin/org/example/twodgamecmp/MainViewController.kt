package org.example.twodgamecmp

import androidx.compose.ui.window.ComposeUIViewController
import org.example.twodgamecmp.game.di.initializeKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initializeKoin() }
) { App() }