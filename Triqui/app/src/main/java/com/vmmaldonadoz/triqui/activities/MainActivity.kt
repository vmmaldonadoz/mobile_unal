package com.vmmaldonadoz.triqui.activities

import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.vmmaldonadoz.triqui.R
import com.vmmaldonadoz.triqui.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this,
                R.layout.activity_main)
    }

    private val boardButtons = arrayListOf<Button>()

    private val game = TicTacToeGame()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root

        boardButtons.add(binding.buttonOne)
        boardButtons.add(binding.buttonTwo)
        boardButtons.add(binding.buttonThree)
        boardButtons.add(binding.buttonFour)
        boardButtons.add(binding.buttonFive)
        boardButtons.add(binding.buttonSix)
        boardButtons.add(binding.buttonSeven)
        boardButtons.add(binding.buttonEight)
        boardButtons.add(binding.buttonNine)

        startNewGame()

        binding.buttonRestart.setOnClickListener { startNewGame() }
    }

    private fun startNewGame() {
        game.clearBoard()

        boardButtons.forEachIndexed { index, button ->
            button.text = ""
            button.isEnabled = true
            button.setOnClickListener { handleClick(button, index) }
        }

        showToast("You go first!")
    }

    private fun showToast(text: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, text, length).show()
    }

    private fun handleClick(button: Button, index: Int) {
        if (button.isEnabled) {
            setMove(TicTacToeGame.HUMAN_PLAYER, index)

            var winner = game.checkForWinner()
            if (winner == 0) {
                showToast("Machines's turn")
                val move = game.getComputerMove()
                setMove(TicTacToeGame.COMPUTER_PLAYER, move)
                winner = game.checkForWinner()
            }

            when (winner) {
                0 -> showToast("It's your turn")
                1 -> showToast("It's a tie")
                2 -> showToast("You won!")
                else -> showToast("Machine won!")
            }
        }
    }

    private fun setMove(player: Char, move: Int) {
        game.setMove(player, move)

        boardButtons[move].let { button ->
            button.isEnabled = false
            button.text = player.toString()
            if (player == TicTacToeGame.HUMAN_PLAYER) {
                button.setTextColor(Color.rgb(0, 200, 0))
            } else {
                button.setTextColor(Color.rgb(200, 0, 0))
            }
        }
    }
}
