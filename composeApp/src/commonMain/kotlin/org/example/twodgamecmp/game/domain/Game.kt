package org.example.twodgamecmp.game.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

data class Game(
    val screenWidth:Int = 0,
    val screenHeight:Int = 0,
    val gravity:Float = 0.8f,
    val size:Float = 30f,
    val jetImpulse:Float = -12f,
    val jetMaxVelocity:Float = 25f,
    val bulletWidth: Float = 150f,
    val bulletSpeed:Float = 5f,
    val bulletGapSpace :Float = 250f
) {
    var status by mutableStateOf(GameStatus.Idle)
        private set
    var jetVelocity by mutableStateOf(0f)
        private set
    var fighterJet by mutableStateOf(
        FighterJet(
            x = (screenWidth/4).toFloat(),
            y = (screenHeight/2).toFloat(),
            size = size
        )
    )
        private set
    val bulletPairs = mutableListOf<BulletPair>()


    fun start(){
        status = GameStatus.Started
    }
    fun gameOver(){
        status = GameStatus.Over
    }
    fun jump(){
        jetVelocity = jetImpulse
    }

    fun restart(){
        resetJetPosition()
        removeBullets()
        start()
    }
    private fun removeBullets(){
        bulletPairs.clear()
    }
    private fun resetJetPosition(){
        fighterJet = fighterJet.copy(
            y = (screenHeight/2).toFloat()
        )
        jetVelocity = 0f
    }
    fun updateGameProgress(){
        if (fighterJet.y < 0){
            stopTheJetFormGoingOut()
            return
        }else if (fighterJet.y > screenHeight){
            gameOver()
            return
        }
        jetVelocity = (jetVelocity + gravity).coerceIn(-jetMaxVelocity,jetMaxVelocity)
        fighterJet = fighterJet.copy(y = fighterJet.y + jetVelocity)
        addNewBullets()
    }
    fun stopTheJetFormGoingOut(){
        jetVelocity = 0f
        fighterJet = fighterJet.copy(y = 0f)

    }
    fun addNewBullets(){
        bulletPairs.forEach { it.x -= bulletSpeed }
        bulletPairs.removeAll{it.x + bulletWidth < 0}
        if (bulletPairs.isEmpty() || bulletPairs.last().x < screenWidth/2){
            val initialBulletX = screenWidth.toFloat() + bulletWidth
            val topHeight = Random.nextFloat()*(screenHeight/2)
            val bottomHeight = screenHeight - topHeight - bulletGapSpace
            val newBulletPair = BulletPair(
                x = initialBulletX,
                y = topHeight + bulletGapSpace/2,
                topHeight = topHeight,
                bottomHeight = bottomHeight
            )
            bulletPairs.add(newBulletPair)
        }
    }
}