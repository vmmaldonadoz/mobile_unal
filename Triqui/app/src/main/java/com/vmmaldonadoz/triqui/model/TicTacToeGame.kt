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

    fun setDifficulty(difficulty: DifficultyLevel) {
        difficultyLevel = difficulty
    }

    // Check for a winner.  Return
    //  0 if no winner or tie yet
    //  1 if it's a tie
    //  2 if X won
    //  3 if O won
    fun checkForWinner(): Int {

        // Check horizontal wins
        kotlin.run {
            var index = 0
            while (index <= 6) {
                if (board[index] == HUMAN_PLAYER &&
                        board[index + 1] == HUMAN_PLAYER &&
                        board[index + 2] == HUMAN_PLAYER)
                    return 2
                if (board[index] == COMPUTER_PLAYER &&
                        board[index + 1] == COMPUTER_PLAYER &&
                        board[index + 2] == COMPUTER_PLAYER)
                    return 3
                index += 3
            }
        }

        // Check vertical wins
        for (i in 0..2) {
            if (board[i] == HUMAN_PLAYER &&
                    board[i + 3] == HUMAN_PLAYER &&
                    board[i + 6] == HUMAN_PLAYER)
                return 2
            if (board[i] == COMPUTER_PLAYER &&
                    board[i + 3] == COMPUTER_PLAYER &&
                    board[i + 6] == COMPUTER_PLAYER)
                return 3
        }

        // Check for diagonal wins
        if (board[0] == HUMAN_PLAYER &&
                board[4] == HUMAN_PLAYER &&
                board[8] == HUMAN_PLAYER || board[2] == HUMAN_PLAYER &&
                board[4] == HUMAN_PLAYER &&
                board[6] == HUMAN_PLAYER)
            return 2
        if (board[0] == COMPUTER_PLAYER &&
                board[4] == COMPUTER_PLAYER &&
                board[8] == COMPUTER_PLAYER || board[2] == COMPUTER_PLAYER &&
                board[4] == COMPUTER_PLAYER &&
                board[6] == COMPUTER_PLAYER)
            return 3

        // Check for tie
        for (i in 0 until BOARD_SIZE) {
            // If we find a number, then no one has won yet
            if (board[i] != HUMAN_PLAYER && board[i] != COMPUTER_PLAYER)
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
            if (board[i] != HUMAN_PLAYER && board[i] != COMPUTER_PLAYER) {
                val curr = board[i]   // Save the current number
                board[i] = HUMAN_PLAYER
                if (checkForWinner() == 2) {
                    board[i] = COMPUTER_PLAYER
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
            if (board[i] != HUMAN_PLAYER && board[i] != COMPUTER_PLAYER) {
                val curr = board[i]
                board[i] = COMPUTER_PLAYER
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
        } while (board[randomMove] == HUMAN_PLAYER || board[randomMove] == COMPUTER_PLAYER)
        return randomMove
    }

    companion object {
        const val HUMAN_PLAYER = 'X'
        const val COMPUTER_PLAYER = 'O'
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