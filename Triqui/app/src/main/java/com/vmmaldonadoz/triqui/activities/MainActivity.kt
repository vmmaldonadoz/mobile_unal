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
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import com.vmmaldonadoz.triqui.R
import com.vmmaldonadoz.triqui.constants.GAME_ID
import com.vmmaldonadoz.triqui.constants.PREFERENCE_NICKNAME_KEY
import com.vmmaldonadoz.triqui.databinding.ActivityMainBinding
import com.vmmaldonadoz.triqui.model.TicTacToeGame
import com.vmmaldonadoz.triqui.viewmodel.MainViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this,
                R.layout.activity_main)
    }

    private val boardButtons = arrayListOf<Button>()

    private lateinit var viewModel: MainViewModel

    private var soundOn: Boolean = true

    private var mediaPlayer: MediaPlayer? = null

    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    private val clickSubject = PublishSubject.create<Int>()

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root

        setupViewModel()
        readExtras(intent)

        bindButtons()
        initResetListener()

        observeBoard()
        observeWinner()
        observeTurn()
    }

    private fun observeTurn() {
        viewModel.turn.observe(this, Observer {
            val safeTurn = it ?: true
            enableBoardButtons(safeTurn)
        })
    }

    private fun enableBoardButtons(playerTurn: Boolean) {
        boardButtons.forEach { button ->
            button.isEnabled = button.text.isBlank() && playerTurn
        }
    }

    private fun readExtras(intent: Intent) {
        val gameId = intent.getStringExtra(GAME_ID).orEmpty()
        val playerName = preferences.getString(PREFERENCE_NICKNAME_KEY, "").orEmpty()

        viewModel.apply {
            setGameId(gameId)
            this.playerName = (playerName)
            setPlayingOnline(gameId.isNotBlank())

        }
        val onlineVisibility = if (viewModel.isPlayingOnline()) GONE else VISIBLE
        binding.buttonRestart.visibility = onlineVisibility
        binding.textViewOrientation.visibility = onlineVisibility
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.pop)
        compositeDisposable.add(
                clickSubject
                        .debounce(400, TimeUnit.MILLISECONDS)
                        .subscribe {
                            handleClick(it)
                        }
        )
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.release()
        compositeDisposable.clear()
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
            if (player == TicTacToeGame.PLAYER_1) {
                button.setTextColor(Color.rgb(0, 200, 0))
            } else {
                button.setTextColor(Color.rgb(200, 0, 0))
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        lifecycle.addObserver(viewModel)
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

        val playingOffline = !viewModel.isPlayingOnline()
        if (playingOffline) {
            val inflater = menuInflater
            inflater.inflate(R.menu.main_menu, menu)
        }

        return playingOffline
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (!viewModel.isPlayingOnline()) {

            when (item?.itemId) {
                R.id.new_game -> startNewGame()
                R.id.settings -> openSettings()
                R.id.ai_difficulty -> showDifficultyDialog()
                R.id.quit -> showQuitDialog()
            }

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
            clickSubject.onNext(index)
        }
    }

    private fun handleClick(index: Int) {
        playClickSound()
        setHumanMovement(index)
        setMachineMovement()
    }

    private fun playClickSound() {
        if (soundOn) {
            mediaPlayer?.start()
        }
    }

    private fun setHumanMovement(index: Int) {
        viewModel.setHumanMovement(index)
    }

    private fun setMachineMovement() {
        if (!viewModel.isPlayingOnline()) {
            viewModel.setMachineMovement()
        }
    }

    private fun checkWinner(winner: Int) {
        when (winner) {
            0 -> showInformation(getPlayersTurn())
            1 -> showInformation(resources.getString(R.string.its_a_tie))
            2 -> showInformation(getPlayerOneWins())
            else -> showInformation(getPlayerTwoWins())
        }
        if (winner > 0) {
            boardButtons.forEach { button ->
                button.isEnabled = false
            }
        }
    }

    private fun getPlayersTurn(): String {
        return if (viewModel.isPlayingOnline()) {
            if (viewModel.remoteGame.gameTurn.isBlank()) {
                if (viewModel.remoteGame.playerOne.equals(viewModel.playerName, true)) {
                    resources.getString(R.string.waiting_for_player)
                } else {
                    resources.getString(R.string.its_your_turn)
                }
            } else {
                resources.getString(R.string.player_turn, viewModel.remoteGame.gameTurn)
            }
        } else {
            resources.getString(R.string.its_your_turn)
        }
    }

    private fun getPlayerTwoWins(): String {
        return if (viewModel.isPlayingOnline()) {
            resources.getString(R.string.player_wins, viewModel.remoteGame.playerTwo)
        } else {
            resources.getString(R.string.machine_won)
        }
    }

    private fun getPlayerOneWins(): String {
        return if (viewModel.isPlayingOnline()) {
            resources.getString(R.string.player_wins, viewModel.remoteGame.playerOne)
        } else {
            getHumanWinsMessage()
        }
    }

    private fun getHumanWinsMessage(): String {
        val defaultMessage = resources.getString(R.string.you_won)
        return PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString("victory_message", defaultMessage).orEmpty()
    }

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
