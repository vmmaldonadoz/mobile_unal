package com.vmmaldonadoz.triqui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.vmmaldonadoz.triqui.model.DifficultyLevel
import com.vmmaldonadoz.triqui.model.TicTacToeGame

class MainViewModel : ViewModel() {

    private val game by lazy { TicTacToeGame() }

    val winner: MutableLiveData<Int> = MutableLiveData()

    val board: MutableLiveData<CharArray> = MutableLiveData()

    init {
        updateBoard()
        updateWinner()
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
            setMovement(TicTacToeGame.COMPUTER_PLAYER, game.getComputerMove())
        }
    }

    private fun updateWinner() {
        winner.postValue(game.checkForWinner())
    }

    private fun updateBoard() {
        board.postValue(game.getBoard())
    }

    fun setHumanMovement(index: Int) {
        setMovement(TicTacToeGame.HUMAN_PLAYER, index)
    }

    private fun setMovement(turn: Char, index: Int) {
        game.setMove(turn, index)
        updateBoard()
        updateWinner()
    }
}