package com.vmmaldonadoz.triqui.mappings

import com.google.firebase.database.DataSnapshot
import com.vmmaldonadoz.triqui.constants.FINISHED
import com.vmmaldonadoz.triqui.constants.PLAYER_1
import com.vmmaldonadoz.triqui.constants.PLAYER_2
import com.vmmaldonadoz.triqui.constants.SAVED_GAME
import com.vmmaldonadoz.triqui.model.RemoteGame
import com.vmmaldonadoz.triqui.utils.tryOrDefault

fun RemoteGame.toMap(): HashMap<String, Any> {
    val game = this

    return HashMap<String, Any>().apply {
        this[PLAYER_1] = game.playerOne
        this[PLAYER_2] = game.playerTwo
        this[SAVED_GAME] = game.savedGame
    }
}

fun DataSnapshot.toRemoteGame(): RemoteGame {
    val gameId = this.key.orEmpty()

    val map = tryOrDefault({ this.value as HashMap<*, *> }, HashMap<String, Any>())

    val playerOne = tryOrDefault({ map[PLAYER_1] as String }, "")
    val playerTwo = tryOrDefault({ map[PLAYER_2] as String }, "")
    val savedGame = tryOrDefault({ map[SAVED_GAME] as String }, "")
    val finished = tryOrDefault({ map[FINISHED] as Boolean }, false)

    return RemoteGame(gameId, playerOne, playerTwo, savedGame, finished)
}