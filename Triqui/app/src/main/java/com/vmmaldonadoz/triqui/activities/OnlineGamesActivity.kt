package com.vmmaldonadoz.triqui.activities

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.vmmaldonadoz.triqui.R
import com.vmmaldonadoz.triqui.constants.GAME_ID
import com.vmmaldonadoz.triqui.controllers.GameListController
import com.vmmaldonadoz.triqui.databinding.ActivityOnlineGamesBinding
import com.vmmaldonadoz.triqui.gameItem
import com.vmmaldonadoz.triqui.model.RemoteGame
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class OnlineGamesActivity : AppCompatActivity() {

    private val binding: ActivityOnlineGamesBinding by lazy {
        DataBindingUtil.setContentView<ActivityOnlineGamesBinding>(this,
                R.layout.activity_online_games)
    }

    private val gamesController by lazy { GameListController() }

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root

        testEpoxy()
        binding.buttonNewGame.setOnClickListener { startNewGame() }
    }

    override fun onResume() {
        super.onResume()
        gamesController.connect()
        compositeDisposable.add(
                gamesController.getGames()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(::showGames, Throwable::printStackTrace)
        )
    }

    private fun showGames(games: List<RemoteGame>) {
        binding.recyclerView.withModels {
            games.forEach { game ->
                gameItem {
                    id(game.gameId)
                    model(game)
                    onClick { _ -> openGameInProgress(game.gameId) }
                }
            }
        }
    }

    private fun openGameInProgress(gameId: String) {
        openGame(gameId)
    }

    override fun onPause() {
        super.onPause()
        gamesController.disconnect()
        compositeDisposable.clear()
    }

    private fun startNewGame() {
        val randomUuid = UUID.randomUUID()

        openGame(randomUuid.toString().takeLast(4))
    }

    private fun openGame(gameId: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(GAME_ID, gameId)
        startActivity(intent)
    }

    private fun testEpoxy() {
        binding.recyclerView.withModels {
            val remoteModel = RemoteGame("id", "playerOne", "playerTwo", "", "playerOne", false)
            for (i in 0 until 2) {
                gameItem {
                    id("data binding $i")
                    model(remoteModel)
                    onClick { _ ->
                        Toast.makeText(this@OnlineGamesActivity, "clicked", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}

fun EpoxyRecyclerView.withModels(buildModelsCallback: EpoxyController.() -> Unit) {
    setControllerAndBuildModels(object : EpoxyController() {
        override fun buildModels() {
            buildModelsCallback()
        }
    })
}


