package org.example.twodgamecmp

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.stevdza_san.sprite.component.drawSpriteView
import com.stevdza_san.sprite.domain.SpriteSheet
import com.stevdza_san.sprite.domain.SpriteSpec
import com.stevdza_san.sprite.domain.rememberSpriteState
import org.example.twodgamecmp.game.domain.Game
import org.example.twodgamecmp.game.domain.GameStatus
import org.example.twodgamecmp.game.util.gameFont
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import twodgamecmp.composeapp.generated.resources.Res
import twodgamecmp.composeapp.generated.resources.background
import twodgamecmp.composeapp.generated.resources.sprite_a


const val JET_WIDTH = 294
const val JET_HEIGHT = 200

@Composable
@Preview
fun App() {
    MaterialTheme {
        var screenWidth by remember { mutableStateOf(0) }
        var screenHeight by remember { mutableStateOf(0) }
        var game by remember { mutableStateOf(Game()) }
        val spriteState = rememberSpriteState(
            totalFrames = 9,
            framesPerRow = 9
        )
        val spriteSpec = remember {
            SpriteSpec(
                screenWidth = screenWidth.toFloat(),
                default = SpriteSheet(
                    frameWidth = JET_WIDTH,
                    frameHeight = JET_HEIGHT,
                    image = Res.drawable.sprite_a
                )
            )
        }
        val currentFrame by spriteState.currentFrame.collectAsState()
        val sheetImage = spriteSpec.imageBitmap
        val animatedAngle by animateFloatAsState(
            targetValue = when {
                game.jetVelocity > game.jetMaxVelocity / 1.1 -> 30f
                else -> 0f
            }
        )


        LaunchedEffect(game.status) {
            while (game.status == GameStatus.Started) {
                withFrameMillis {
                    game.updateGameProgress()
                }
            }
            if (game.status == GameStatus.Over) {
                spriteState.stop()

            }
        }
        val backgroundOffsetX = remember { Animatable(0f) }
        var imageWidth by remember { mutableStateOf(0) }
        LaunchedEffect(game.status) {
            backgroundOffsetX.snapTo(0f)  // added for reset background image animate offset
            while (game.status == GameStatus.Started) {
                backgroundOffsetX.animateTo(
                    targetValue = -imageWidth.toFloat(),
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            4000,
                            easing = LinearEasing
                        ), repeatMode = RepeatMode.Restart
                    )
                )
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                spriteState.stop()
                spriteState.cleanup()
            }
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
//            Image(
//                modifier = Modifier.fillMaxSize(),
//                painter = painterResource(Res.drawable.background),
//                contentDescription = "Background Image",
//                contentScale = ContentScale.Crop
//            )
            Image(
                modifier = Modifier.fillMaxSize()
                    .onSizeChanged {
                        imageWidth = it.width
                    }.offset {
                        IntOffset(
                            x = backgroundOffsetX.value.toInt(),
                            y = 0
                        )
                    },
                painter = painterResource(Res.drawable.background),
                contentDescription = "Moving Background Image",
                contentScale = ContentScale.FillHeight
            )
            Image(
                modifier = Modifier.fillMaxSize()
                    .offset {
                        IntOffset(
                            x = backgroundOffsetX.value.toInt() + imageWidth,
                            y = 0
                        )
                    },
                painter = painterResource(Res.drawable.background),
                contentDescription = "Moving Background Image",
                contentScale = ContentScale.FillHeight
            )

        }
        androidx.compose.foundation.Canvas(
            modifier = Modifier.fillMaxSize()
                .onGloballyPositioned {
                    val size = it.size
                    if (screenWidth != size.width || screenHeight != size.height) {
                        screenWidth = size.width
                        screenHeight = size.height
                        game = game.copy(
                            screenWidth = size.width,
                            screenHeight = size.height
                        )
                    }
                }
                .clickable {
                    if (game.status == GameStatus.Started) {
                        game.jump()
                    }
                }
        ) {
            rotate(
                degrees = animatedAngle,
                pivot = Offset(
                    x = game.fighterJet.x - game.size,
                    y = game.fighterJet.y - game.size,
                )
            ) {
                drawSpriteView(
                    spriteState = spriteState,
                    spriteSpec = spriteSpec,
                    currentFrame = currentFrame,
                    image = sheetImage,
                    offset = IntOffset(
                        x = (game.fighterJet.x - game.size).toInt(),
                        y = (game.fighterJet.y - game.size).toInt()
                    )
                )
            }
            game.bulletPairs.forEach { bulletPair ->
                drawRect(
                    color = Color.Blue,
                    topLeft = Offset(
                        x = bulletPair.x - game.bulletWidth/2,
                        y = 0f
                    ),
                    size = Size(game.bulletWidth, bulletPair.topHeight)
                )
                drawRect(
                    color = Color.Blue,
                    topLeft = Offset(
                        x = bulletPair.x - game.bulletWidth/2,
                        y = bulletPair.y + game.bulletGapSpace/2
                    ),
                    size = Size(game.bulletWidth, bulletPair.bottomHeight)
                )
            }

//            drawCircle(
//                color = Color.Blue,
//                radius = game.fighterJet.size,
//                center = Offset(
//                    x = game.fighterJet.x,
//                    y = game.fighterJet.y
//                )
//            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(all = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Best: 0",
                fontWeight = FontWeight.Bold,
                fontFamily = gameFont(),
                fontSize = MaterialTheme.typography.displaySmall.fontSize
            )
            Text(
                text = "0",
                fontWeight = FontWeight.Bold,
                fontFamily = gameFont(),
                fontSize = MaterialTheme.typography.displaySmall.fontSize
            )
        }
        if (game.status == GameStatus.Idle) {
            Box(
                modifier = Modifier.fillMaxSize().background(color = Color.Black.copy(0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    modifier = Modifier.height(54.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(contentColor = Color.Blue),
                    onClick = {
                        game.start()
                        spriteState.start()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "start",
                        tint = Color.Green
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Start",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontFamily = gameFont()
                    )
                }

            }
        }
        if (game.status == GameStatus.Over) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(color = Color.Black.copy(0.4f)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Game Over!",
                    color = Color.Blue,
                    fontSize = MaterialTheme.typography.displayMedium.fontSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = gameFont()
                )
                Text(
                    "Score :0",
                    color = Color.Blue,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = gameFont()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    modifier = Modifier.height(54.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(contentColor = Color.Blue),
                    onClick = {
                        game.restart()
                        spriteState.start()
                    }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart",
                        tint = Color.Green
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Restart",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontFamily = gameFont()
                    )
                }

            }
        }

//        val screenWidth = getScreenWidth()
//        val spriteState = rememberSpriteState(
//            totalFrames = 9,
//            framesPerRow = 9
//        )
//
//        val spriteSpec = remember {
////            SpriteSpec(
////                screenWidth = screenWidth.value,
////                default = SpriteSheet(
////                    frameWidth = 1507,
////                    frameHeight = 1017,
////                    image = Res.drawable.sprite
////                )
////            )
//
//            SpriteSpec(
//                screenWidth = screenWidth.value,
//                default = SpriteSheet(
//                    frameWidth = 294,
//                    frameHeight = 200,
//                    image = Res.drawable.sprite_a
//                )
//            )
//        }
//        DisposableEffect(Unit){
//            spriteState.start()
//            onDispose {
//                spriteState.stop()
//                spriteState.cleanup()
//            }
//        }
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ){
//            SpriteView(
//                spriteSpec = spriteSpec, spriteState = spriteState
//            )
//
//        }

    }
}