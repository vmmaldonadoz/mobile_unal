package com.vmmaldonadoz.triqui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.vmmaldonadoz.triqui.model.DifficultyLevel
import com.vmmaldonadoz.triqui.model.TicTacToeGame

class MainViewModel : ViewModel() {

    private var playingOnline = false
    private var gameId = ""

    private val game by lazy { TicTacToeGame() }

    val winner: MutableLiveData<Int> = MutableLiveData()

    val board: MutableLiveData<CharArray> = MutableLiveData()

    init {
        updateBoard()
        updateWinner()
    }

    fun isPlayingOnline(): Boolean {
        return playingOnline
    }

    fun setEasyDifficulty() {
        game.setDifficulty(DifficultyLevel.Easy)
    }

    fun setHarderDifficulty() {
        game.setDifficulty(DifficultyLevel.Harder)
    }

    fun setExpertDifficulty() {
        game.setDifficulty(DifficultyLevel.Expert)
    }

    fun startNewGame() {
        game.clearBoard()
        updateBoard()
        updateWinner()
    }

    fun setMachineMovement() {
        if (game.checkForWinner() == 0) {
            setMovement(TicTacToeGame.PLAYER_2, game.getComputerMove())
        }
    }

    private fun updateWinner() {
        winner.postValue(game.checkForWinner())
    }

    private fun updateBoard() {
        board.postValue(game.getBoard())
    }

    fun setHumanMovement(index: Int) {
        setMovement(TicTacToeGame.PLAYER_1, index)
    }

    private fun setMovement(turn: Char, index: Int) {
        game.setMove(turn, index)
        updateBoard()
        updateWinner()
    }

    fun setPlayingOnline(playingOnline: Boolean) {
        this.playingOnline = playingOnline
    }

    fun setGame(gameId: String) {
        this.gameId = gameId
    }
}