package com.vmmaldonadoz.triqui.model

import java.util.*

class TicTacToeGame {

    private var board = CharArray(9) { ' ' }
    private val BOARD_SIZE = 9

    private val mRand: Random = Random()

    private var difficultyLevel = DifficultyLevel.Expert

    init {
        initBoard()
    }

    private fun initBoard() {
        board = CharArray(9) { ' ' }
    }

    fun loadSavedGame(savedGame : CharArray){
        board = savedGame
    }

    fun setDifficulty(difficulty: DifficultyLevel) {
        difficultyLevel = difficulty
    }

    // Check for a winner.  Return
    //  0 if no winner or tie yet
    //  1 if it's a tie
    //  2 if X won (Player 1)
    //  3 if O won (Player 2)
    fun checkForWinner(): Int {

        // Check horizontal wins
        kotlin.run {
            var index = 0
            while (index <= 6) {
                if (board[index] == PLAYER_1 &&
                        board[index + 1] == PLAYER_1 &&
                        board[index + 2] == PLAYER_1)
                    return 2
                if (board[index] == PLAYER_2 &&
                        board[index + 1] == PLAYER_2 &&
                        board[index + 2] == PLAYER_2)
                    return 3
                index += 3
            }
        }

        // Check vertical wins
        for (i in 0..2) {
            if (board[i] == PLAYER_1 &&
                    board[i + 3] == PLAYER_1 &&
                    board[i + 6] == PLAYER_1)
                return 2
            if (board[i] == PLAYER_2 &&
                    board[i + 3] == PLAYER_2 &&
                    board[i + 6] == PLAYER_2)
                return 3
        }

        // Check for diagonal wins
        if (board[0] == PLAYER_1 &&
                board[4] == PLAYER_1 &&
                board[8] == PLAYER_1 || board[2] == PLAYER_1 &&
                board[4] == PLAYER_1 &&
                board[6] == PLAYER_1)
            return 2
        if (board[0] == PLAYER_2 &&
                board[4] == PLAYER_2 &&
                board[8] == PLAYER_2 || board[2] == PLAYER_2 &&
                board[4] == PLAYER_2 &&
                board[6] == PLAYER_2)
            return 3

        // Check for tie
        for (i in 0 until BOARD_SIZE) {
            // If we find a number, then no one has won yet
            if (board[i] != PLAYER_1 && board[i] != PLAYER_2)
                return 0
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1
    }

    fun getComputerMove(): Int {
        var move: Int = -1

        move = when (difficultyLevel) {
            DifficultyLevel.Expert -> moveLikeAnExpert()
            DifficultyLevel.Harder -> getWinningMove()
            DifficultyLevel.Easy -> getRandomMove()
        }

        if (move == -1) {
            move = getRandomMove()
        }
        return move
    }

    private fun moveLikeAnExpert(): Int {
        var move = getWinningMove()
        if (move == -1) {
            move = getBlockingMove()
        }
        return move
    }

    private fun getBlockingMove(): Int {
        // See if there's a move O can make to block X from winning
        for (i in 0 until BOARD_SIZE) {
            if (board[i] != PLAYER_1 && board[i] != PLAYER_2) {
                val curr = board[i]   // Save the current number
                board[i] = PLAYER_1
                if (checkForWinner() == 2) {
                    board[i] = PLAYER_2
                    println("Computer is moving to " + (i + 1))
                    return i
                } else {
                    board[i] = curr
                }
            }
        }
        return -1
    }

    private fun getWinningMove(): Int {
        // First see if there's a move O can make to win
        for (i in 0 until BOARD_SIZE) {
            if (board[i] != PLAYER_1 && board[i] != PLAYER_2) {
                val curr = board[i]
                board[i] = PLAYER_2
                if (checkForWinner() == 3) {
                    return i
                } else {
                    board[i] = curr
                }
            }
        }
        return -1
    }

    private fun getRandomMove(): Int {
        var randomMove: Int
        do {
            randomMove = mRand.nextInt(BOARD_SIZE)
        } while (board[randomMove] == PLAYER_1 || board[randomMove] == PLAYER_2)
        return randomMove
    }

    companion object {
        const val PLAYER_1 = 'X'
        const val PLAYER_2 = 'O'
    }

    fun clearBoard() {
        initBoard()
    }

    fun setMove(player: Char, location: Int) {
        board[location] = player
    }

    fun getBoard(): CharArray {
        return board
    }
}