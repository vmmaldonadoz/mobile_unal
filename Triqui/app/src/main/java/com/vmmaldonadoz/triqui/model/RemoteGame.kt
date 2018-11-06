package com.vmmaldonadoz.triqui.model

data class RemoteGame(val gameId: String,
                      val playerOne: String,
                      val playerTwo: String,
                      val savedGame: String,
                      val gameTurn: String,
                      val finished: Boolean)