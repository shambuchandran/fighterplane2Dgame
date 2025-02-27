package org.example.twodgamecmp.game.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.ObservableSettings
import org.example.twodgamecmp.game.util.Platform
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random

const val SCORE_KEY = "score"

data class Game(
    val platform: Platform,
    val screenWidth:Int = 0,
    val screenHeight:Int = 0,
    val gravity:Float = 0.5f,
    val size:Float = 30f,
    val jetImpulse:Float = -10f,
    val jetMaxVelocity:Float = if (platform == Platform.Android)25f else 10f,
    val bulletWidth: Float = 150f,
    val bulletSpeed:Float = if (platform == Platform.Android)5f else 2.5f,
    val bulletGapSpace :Float = if (platform == Platform.Android)250f else 300f
): KoinComponent {

    private val audioPlayer :AudioPlayer by inject()
    private val settings:ObservableSettings by inject()
    var status by mutableStateOf(GameStatus.Idle)
        private set
    var jetVelocity by mutableStateOf(0f)
        private set
    var fighterJet by mutableStateOf(
        FighterJet(
            x = (screenWidth/6).toFloat(),
            y = (screenHeight/2).toFloat(),
            size = size
        )
    )
        private set
    val bulletPairs = mutableListOf<BulletPair>()
    var currentScore by mutableStateOf(0)
        private set
    var bestScore by mutableStateOf(0)
        private set
    private var isFallingSoundPlayer = false

    init {
        bestScore = settings.getInt(
            key = SCORE_KEY,
            defaultValue = 0
        )
        settings.addIntListener(
            key = SCORE_KEY,
            defaultValue = 0
        ){
            bestScore = it
        }
    }



    fun start(){
        status = GameStatus.Started
        audioPlayer.playGameSoundInLoop()
    }
    fun gameOver(){
        status = GameStatus.Over
        audioPlayer.stopGameSound()
        saveScore()
        isFallingSoundPlayer = false
    }
    private fun saveScore(){
        if (bestScore < currentScore){
            settings.putInt(key = SCORE_KEY, value = currentScore)
            bestScore = currentScore
        }
    }
    fun jump(){
        jetVelocity = jetImpulse
        audioPlayer.playJumpSound()
        isFallingSoundPlayer = false
    }

    fun restart(){
        resetJetPosition()
        removeBullets()
        resetScore()
        start()
        isFallingSoundPlayer = false
    }
    private fun removeBullets(){
        bulletPairs.clear()
    }
    private fun resetScore(){
        currentScore = 0
    }
    private fun resetJetPosition(){
        fighterJet = fighterJet.copy(
            y = (screenHeight/2).toFloat()
        )
        jetVelocity = 0f
    }
    fun updateGameProgress(){
        bulletPairs.forEach { bulletPair ->
            if (isCollision(bulletPair)){
                gameOver()
                return
            }
            if (!bulletPair.score && fighterJet.x > bulletPair.x + bulletWidth/2){
                bulletPair.score = true
                currentScore += 1
            }
        }
        if (fighterJet.y < 0){
            stopTheJetFormGoingOut()
            return
        }else if (fighterJet.y > screenHeight){
            gameOver()
            return
        }
        jetVelocity = (jetVelocity + gravity).coerceIn(-jetMaxVelocity,jetMaxVelocity)
        fighterJet = fighterJet.copy(y = fighterJet.y + jetVelocity)

        //when to play fall sound
        if (jetVelocity > (jetMaxVelocity/1.1)){
            if (!isFallingSoundPlayer) {
                audioPlayer.playFallingSound()
                 isFallingSoundPlayer = true
            }
        }

        addNewBullets()
    }

    private fun isCollision(bulletPair: BulletPair):Boolean{
        //horizontal collision
        val fighterJetRightEdge = fighterJet.x + fighterJet.size
        val fighterJetLeftEdge = fighterJet.y + fighterJet.size
        val bulletRightEdge = bulletPair.x + bulletWidth/2
        val bulletLeftEdge = bulletPair.x + bulletWidth/2
        val horizontalCollision = fighterJetRightEdge > bulletLeftEdge && fighterJetLeftEdge < bulletRightEdge

        //gap collision
        val fighterJetTopEdge = fighterJet.y + fighterJet.size
        val fighterJetBottomEdge = fighterJet.y + fighterJet.size
        val gapTopEdge = bulletPair.y - bulletGapSpace/2
        val gapBottomEdge = bulletPair.y + bulletGapSpace/2
        val fighterJetInGap = fighterJetTopEdge > gapTopEdge && fighterJetBottomEdge < gapBottomEdge

        return horizontalCollision && !fighterJetInGap
    }

    fun stopTheJetFormGoingOut(){
        jetVelocity = 0f
        fighterJet = fighterJet.copy(y = 0f)

    }
    fun addNewBullets(){
        bulletPairs.forEach { it.x -= bulletSpeed }
        bulletPairs.removeAll{it.x + bulletWidth < 0}
        val isLandscape = screenWidth > screenHeight
        val addThreshold = if (isLandscape) screenWidth /1.25 else screenWidth/2.0
        if (bulletPairs.isEmpty() || bulletPairs.last().x < addThreshold){
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
    fun cleanUp(){
        audioPlayer.release()
    }
}