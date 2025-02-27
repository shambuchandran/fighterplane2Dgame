package org.example.twodgamecmp.game.domain

data class BulletPair(
    var x :Float,
    val y:Float,
    val topHeight:Float,
    val bottomHeight:Float,
    var score :Boolean = false
)
