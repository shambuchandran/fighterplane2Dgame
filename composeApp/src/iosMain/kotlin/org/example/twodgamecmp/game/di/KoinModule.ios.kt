package org.example.twodgamecmp.game.di

import org.example.twodgamecmp.game.domain.AudioPlayer
import org.koin.dsl.module

actual val targetModule = module {
    single<AudioPlayer> { AudioPlayer() }
}

//want to add all sound in ios directory is pending.