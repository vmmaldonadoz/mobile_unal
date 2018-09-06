package com.vmmaldonadoz.triqui.activities

import java.util.*

class TicTacToeGame {

    private var mBoard = charArrayOf()
    private val BOARD_SIZE = 9

    private val mRand: Random = Random()

    init {
        initBoard()
    }

    private fun initBoard() {
        mBoard = charArrayOf('1', '2', '3', '4', '5', '6', '7', '8', '9')
    }

    // Check for a winner.  Return
    //  0 if no winner or tie yet
    //  1 if it's a tie
    //  2 if X won
    //  3 if O won
    fun checkForWinner(): Int {

        // Check horizontal wins
        run {
            var i = 0
            while (i <= 6) {
                if (mBoard[i] == HUMAN_PLAYER &&
                        mBoard[i + 1] == HUMAN_PLAYER &&
                        mBoard[i + 2] == HUMAN_PLAYER)
                    return 2
                if (mBoard[i] == COMPUTER_PLAYER &&
                        mBoard[i + 1] == COMPUTER_PLAYER &&
                        mBoard[i + 2] == COMPUTER_PLAYER)
                    return 3
                i += 3
            }
        }

        // Check vertical wins
        for (i in 0..2) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i + 3] == HUMAN_PLAYER &&
                    mBoard[i + 6] == HUMAN_PLAYER)
                return 2
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i + 3] == COMPUTER_PLAYER &&
                    mBoard[i + 6] == COMPUTER_PLAYER)
                return 3
        }

        // Check for diagonal wins
        if (mBoard[0] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[8] == HUMAN_PLAYER || mBoard[2] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[6] == HUMAN_PLAYER)
            return 2
        if (mBoard[0] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[8] == COMPUTER_PLAYER || mBoard[2] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[6] == COMPUTER_PLAYER)
            return 3

        // Check for tie
        for (i in 0 until BOARD_SIZE) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
                return 0
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1
    }

    fun getComputerMove(): Int {
        var move: Int

        // First see if there's a move O can make to win
        for (i in 0 until BOARD_SIZE) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                val curr = mBoard[i]
                mBoard[i] = COMPUTER_PLAYER
                if (checkForWinner() == 3) {
                    println("Computer is moving to " + (i + 1))
                    return i
                } else {
                    mBoard[i] = curr
                }
            }
        }

        // See if there's a move O can make to block X from winning
        for (i in 0 until BOARD_SIZE) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                val curr = mBoard[i]   // Save the current number
                mBoard[i] = HUMAN_PLAYER
                if (checkForWinner() == 2) {
                    mBoard[i] = COMPUTER_PLAYER
                    println("Computer is moving to " + (i + 1))
                    return i
                } else {
                    mBoard[i] = curr
                }
            }
        }

        // Generate random move
        do {
            move = mRand.nextInt(BOARD_SIZE)
        } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER)

        println("Computer is moving to " + (move + 1))

        mBoard[move] = COMPUTER_PLAYER
        return move
    }

    companion object {

        val HUMAN_PLAYER = 'X'
        val COMPUTER_PLAYER = 'O'
        val OPEN_SPOT = " "
    }

    fun clearBoard() {
        initBoard()
    }

    fun setMove(player: Char, location: Int) {
        mBoard[location] = player
    }
}