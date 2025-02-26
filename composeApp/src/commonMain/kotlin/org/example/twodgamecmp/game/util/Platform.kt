package org.example.twodgamecmp.game.util

enum class Platform{
    Android,
    Ios,
    Desktop,
    Web
}

expect fun getPlatform(): Platform