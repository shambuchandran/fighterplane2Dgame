package org.example.twodgamecmp.game.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.Font
import twodgamecmp.composeapp.generated.resources.Res
import twodgamecmp.composeapp.generated.resources.sunshiney_regular


@Composable
fun gameFont() = FontFamily(Font(Res.font.sunshiney_regular))