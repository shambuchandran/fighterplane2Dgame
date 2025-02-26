package org.example.twodgamecmp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.twodgamecmp.game.util.gameFont
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import twodgamecmp.composeapp.generated.resources.Res
import twodgamecmp.composeapp.generated.resources.background

@Composable
@Preview
fun App() {
    MaterialTheme {

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(Res.drawable.background),
                contentDescription = "Background Image",
                contentScale = ContentScale.Crop
            )
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