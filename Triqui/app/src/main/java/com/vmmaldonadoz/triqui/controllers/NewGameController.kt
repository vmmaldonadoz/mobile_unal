package com.vmmaldonadoz.triqui.controllers

import com.google.firebase.database.FirebaseDatabase
import com.vmmaldonadoz.triqui.constants.GAMES_REFERENCE
import com.vmmaldonadoz.triqui.mappings.toMap
import com.vmmaldonadoz.triqui.model.RemoteGame

class NewGameController {

    private val reference = FirebaseDatabase.getInstance().getReference(GAMES_REFERENCE)

    fun createNewGame(game: RemoteGame) {
        reference.push().setValue(game.toMap())
    }
}