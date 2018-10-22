package com.vmmaldonadoz.triqui.activities

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.vmmaldonadoz.triqui.R
import com.vmmaldonadoz.triqui.databinding.ActivityMainBinding
import com.vmmaldonadoz.triqui.model.TicTacToeGame
import com.vmmaldonadoz.triqui.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this,
                R.layout.activity_main)
    }

    private val boardButtons = arrayListOf<Button>()

    private lateinit var viewModel: MainViewModel

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root

        bindButtons()
        initResetListener()

        setupViewModel()

        observeBoard()
        observeWinner()
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.pop)
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.release()
    }

    private fun observeWinner() {
        viewModel.winner.observe(this, Observer { winner ->
            val safeWinner = winner ?: 0
            checkWinner(safeWinner)
        })
    }

    private fun observeBoard() {
        viewModel.board.observe(this, Observer { board ->
            val safeBoard = board ?: CharArray(9) { ' ' }
            drawBoard(safeBoard)
        })
    }

    private fun drawBoard(safeBoard: CharArray) {
        safeBoard.forEachIndexed { index, movement -> drawMovement(movement, index) }
    }

    private fun drawMovement(player: Char, move: Int) {
        boardButtons[move].let { button ->
            button.isEnabled = player == ' '
            button.text = player.toString()
            if (player == TicTacToeGame.HUMAN_PLAYER) {
                button.setTextColor(Color.rgb(0, 200, 0))
            } else {
                button.setTextColor(Color.rgb(200, 0, 0))
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private fun initResetListener() {
        binding.buttonRestart.setOnClickListener { startNewGame() }
    }

    private fun bindButtons() {
        boardButtons.add(binding.buttonOne)
        boardButtons.add(binding.buttonTwo)
        boardButtons.add(binding.buttonThree)
        boardButtons.add(binding.buttonFour)
        boardButtons.add(binding.buttonFive)
        boardButtons.add(binding.buttonSix)
        boardButtons.add(binding.buttonSeven)
        boardButtons.add(binding.buttonEight)
        boardButtons.add(binding.buttonNine)

        boardButtons.forEachIndexed { index, button ->
            button.text = ""
            button.isEnabled = true
            button.setOnClickListener { handleClick(button, index) }
        }
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
            R.id.settings -> openSettings()
            R.id.ai_difficulty -> showDifficultyDialog()
            R.id.quit -> showQuitDialog()
        }

        return false
    }

    private fun openSettings(): Boolean {
        startActivityForResult(Intent(this, PreferencesActivity::class.java), 0)
        return true
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
                    0 -> viewModel.setEasyDifficulty()
                    1 -> viewModel.setHarderDifficulty()
                    2 -> viewModel.setExpertDifficulty()
                }
                dialogInterface.dismiss()
                startNewGame()
            }
        }
        dialog.create().show()
    }

    private fun startNewGame() {
        viewModel.startNewGame()
    }

    private fun showInformation(text: String) {
        binding.textViewInfo.text = text
    }

    private fun handleClick(button: Button, index: Int) {
        if (button.isEnabled) {
            playHumanClick()
            setHumanMovement(index)
            setMachineMovement()
        }
    }

    private fun playHumanClick() {
        if (soundOn) {
            mediaPlayer?.start()
        }
    }

    private fun setHumanMovement(index: Int) {
        viewModel.setHumanMovement(index)
    }

    private fun setMachineMovement() {
        viewModel.setMachineMovement()
    }

    private fun checkWinner(winner: Int) {
        when (winner) {
            0 -> showInformation(resources.getString(R.string.its_your_turn))
            1 -> showInformation(resources.getString(R.string.its_a_tie))
            2 -> showInformation(getHumanWinsMessage())
            else -> showInformation(resources.getString(R.string.machine_won))
        }
        if (winner > 0) {
            boardButtons.forEach { button ->
                button.isEnabled = false
            }
        }
    }

    private fun getHumanWinsMessage(): String {
        val defaultMessage = resources.getString(R.string.you_won)
        return PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString("victory_message", defaultMessage).orEmpty()
    }

    private var soundOn: Boolean = true

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Activity.RESULT_CANCELED) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
            soundOn = preferences.getBoolean("sound", true)

            val difficultyLevel = preferences.getString("difficulty_level", getString(R.string.difficulty_expert))
            when (difficultyLevel) {
                getString(R.string.difficulty_expert) -> viewModel.setExpertDifficulty()
                getString(R.string.difficulty_harder) -> viewModel.setHarderDifficulty()
                getString(R.string.difficulty_easy) -> viewModel.setEasyDifficulty()
            }
        }
    }
}
