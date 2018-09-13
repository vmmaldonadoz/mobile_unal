package com.vmmaldonadoz.triqui.activities

import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.new_game -> startNewGame()
            R.id.ai_difficulty -> showDifficultyDialog()
            R.id.quit -> showQuitDialog()
        }

        return false
    }

    private fun showQuitDialog() {
        val dialog = AlertDialog.Builder(this).apply {
            setMessage(R.string.quit_question)
            setCancelable(false)
            setPositiveButton(R.string.yes) { _, _ ->
                finish()
            }
            setNegativeButton(R.string.no) { _, _ -> }
        }
        dialog.create().show()
    }

    private fun showDifficultyDialog() {
        val levels = arrayOf<String>(getString(R.string.difficulty_easy), getString(R.string.difficulty_harder), getString(R.string.difficulty_expert))
        val dialog = AlertDialog.Builder(this).apply {
            title = getString(R.string.difficulty_choose)
            setSingleChoiceItems(levels, 0) { dialogInterface, selected ->
                when (selected) {
                    0 -> game.setDifficulty(TicTacToeGame.DificultyLevel.Easy)
                    1 -> game.setDifficulty(TicTacToeGame.DificultyLevel.Harder)
                    2 -> game.setDifficulty(TicTacToeGame.DificultyLevel.Expert)
                }
                dialogInterface.dismiss()
                startNewGame()
            }
        }
        dialog.create().show()
    }

    private fun startNewGame() {
        game.clearBoard()

        boardButtons.forEachIndexed { index, button ->
            button.text = ""
            button.isEnabled = true
            button.setOnClickListener { handleClick(button, index) }
        }

        binding.textViewInfo.text = "You go first!"
    }

    private fun showToast(text: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, text, length).show()
    }

    private fun handleClick(button: Button, index: Int) {
        if (button.isEnabled) {
            setMove(TicTacToeGame.HUMAN_PLAYER, index)

            var winner = game.checkForWinner()
            if (winner == 0) {
                binding.textViewInfo.text = "Machines's turn"
                val move = game.getComputerMove()
                setMove(TicTacToeGame.COMPUTER_PLAYER, move)
                winner = game.checkForWinner()
            }

            when (winner) {
                0 -> binding.textViewInfo.text = "It's your turn"
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
