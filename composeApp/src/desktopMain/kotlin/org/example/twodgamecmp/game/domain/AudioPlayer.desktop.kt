package org.example.twodgamecmp.game.domain

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine
import kotlin.concurrent.thread

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AudioPlayer {
    private val audioCache = mutableMapOf<String, ByteArray>()
    private val playingLines = mutableMapOf<String, SourceDataLine>()


    actual fun playGameOverSound() {
        stopFallingSound()
        playSound("game_over.wav")
    }

    actual fun playJumpSound() {
        stopFallingSound()
        playSound("jump.wav")
    }

    actual fun playFallingSound() {
        playSound("falling.wav")
    }

    actual fun stopFallingSound() {
        stopSound("falling.wav")
    }

    actual fun playGameSoundInLoop() {
        playSound("game_sound.wav", loop = true)
    }

    actual fun stopGameSound() {
        playGameOverSound()
        stopSound("game_sound.wav")

    }

    actual fun release() {
        audioCache.clear()
        stopALlSounds()
    }

    private fun playSound(filename: String, loop: Boolean = false) {
        thread {
            try {
                val audioData = audioCache[filename] ?: loadAudioFile(filename).also {
                    audioCache[filename] = it
                }
                val inputStream = AudioSystem.getAudioInputStream(audioData.inputStream())
                val format = inputStream.format
                val info = DataLine.Info(SourceDataLine::class.java, format)
                val line = AudioSystem.getLine(info) as SourceDataLine
                line.open(format)
                line.start()
                synchronized(playingLines) {
                    playingLines[filename] = line
                }
                val buffer = ByteArray(4096)
                var byteRead = 0
                var shouldContinue = true
                if (loop) {
                    while (shouldContinue) {
                        inputStream.reset()
                        while (shouldContinue && inputStream.read(buffer)
                                .also { byteRead = it } != -1
                        ) {
                            synchronized(playingLines) {
                                shouldContinue = playingLines.containsKey(filename)
                            }
                            if (shouldContinue) {
                                line.write(buffer, 0, byteRead)
                            }
                        }
                    }

                } else {
                    while (shouldContinue && inputStream.read(buffer)
                            .also { byteRead = it } != -1
                    ) {
                        synchronized(playingLines) {
                            shouldContinue = playingLines.containsKey(filename)
                        }
                        if (shouldContinue) {
                            line.write(buffer, 0, byteRead)
                        }
                    }
                    line.drain()
                    line.close()
                    synchronized(playingLines) {
                        playingLines.remove(filename)
                    }

                }

            } catch (e:Exception) {
                println("Error in playing the audio : $filename . $e")
            }
        }
    }

    private fun stopSound(filename: String) {
        synchronized(playingLines) {
            playingLines[filename]?.let {
                it.stop()
                it.close()
                playingLines.remove(filename)
            }
        }
    }

    private fun stopALlSounds() {
        synchronized(playingLines) {
            playingLines.values.forEach {
                it.stop()
                it.close()
            }
            playingLines.clear()
        }
    }

    private fun loadAudioFile(filename: String): ByteArray {
        val resourcePath = Paths.get("src/commonMain/composeResources/files/$filename")
        if (!Files.exists(resourcePath)) {
            throw FileNotFoundException("Resource not found: $resourcePath")
        }
        return FileInputStream(resourcePath.toFile()).use { it.readBytes() }
    }
}