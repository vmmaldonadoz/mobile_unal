package com.vmmaldonadoz.triqui.viewmodel

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ViewModel
import com.vmmaldonadoz.triqui.controllers.GameController
import com.vmmaldonadoz.triqui.mappings.mapToString
import com.vmmaldonadoz.triqui.model.DifficultyLevel
import com.vmmaldonadoz.triqui.model.RemoteGame
import com.vmmaldonadoz.triqui.model.TicTacToeGame
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel : ViewModel(), LifecycleObserver {

    var playerName = ""
    private var playingOnline = false
    private var gameId = ""

    private val game by lazy { TicTacToeGame() }

    val winner: MutableLiveData<Int> = MutableLiveData()

    val board: MutableLiveData<CharArray> = MutableLiveData()

    val turn: MutableLiveData<Boolean> = MutableLiveData()

    private val onlineGameController by lazy { GameController() }

    var remoteGame = RemoteGame("", "", "", "", "", false)

    private val compositeDisposable = CompositeDisposable()

    init {
        updateBoard()
        updateWinner()
        updateTurn()
    }

    private fun updateTurn() {
        turn.postValue(
                !playingOnline
                        || remoteGame.savedGame.isBlank()
                        || remoteGame.gameTurn.equals(playerName, true)
                        || (!remoteGame.playerOne.equals(playerName, true) && remoteGame.playerTwo.isBlank())
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        if (isPlayingOnline()) {
            onlineGameController.connect(gameId)
            compositeDisposable.add(
                    onlineGameController.getGame()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                remoteGame = it
                                game.setRemoteGame(it)
                                updateBoard()
                                updateWinner()
                                updateTurn()
                            }
            )
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        if (isPlayingOnline()) {
            onlineGameController.disconnect()
            compositeDisposable.clear()
        }
    }

    private fun getPlayerSymbol(): Char {
        return if (isPlayingOnline()) {
            when {
                remoteGame.playerOne.equals(playerName, true) ||
                        remoteGame.playerOne.isBlank() -> TicTacToeGame.PLAYER_1
                remoteGame.playerTwo.equals(playerName, true) ||
                        remoteGame.playerTwo.isBlank() -> TicTacToeGame.PLAYER_2
                else -> TicTacToeGame.PLAYER_1
            }
        } else {
            TicTacToeGame.PLAYER_1
        }
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
        setMovement(getPlayerSymbol(), index)
    }

    private fun saveRemoteGame() {
        if (isPlayingOnline()) {
            when {
                remoteGame.playerOne.isBlank() -> remoteGame = remoteGame.copy(playerOne = playerName)
                remoteGame.playerTwo.isBlank() -> remoteGame = remoteGame.copy(playerTwo = playerName)
            }

            val newTurn = if (remoteGame.playerOne.equals(playerName, true)) remoteGame.playerTwo else remoteGame.playerOne
            val newRemoteGame = remoteGame.copy(
                    gameId = gameId,
                    savedGame = game.getBoard().mapToString(),
                    gameTurn = newTurn,
                    finished = game.checkForWinner() > 0
            )
            onlineGameController.saveRemoteGame(newRemoteGame)
        }
    }

    private fun setMovement(turn: Char, index: Int) {
        game.setMove(turn, index)
        saveRemoteGame()
        updateBoard()
        updateWinner()
        updateTurn()
    }

    fun setPlayingOnline(playingOnline: Boolean) {
        this.playingOnline = playingOnline
        onResume()
    }

    fun setGameId(gameId: String) {
        this.gameId = gameId
    }
}