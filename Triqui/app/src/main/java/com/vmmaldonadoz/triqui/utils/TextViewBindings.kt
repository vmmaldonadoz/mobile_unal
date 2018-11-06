package com.vmmaldonadoz.triqui.utils

import android.databinding.BindingAdapter
import android.widget.TextView
import com.vmmaldonadoz.triqui.R

@BindingAdapter("app:online_game_state")
fun TextView.setGameState(gameInProgress: Boolean) {
    val value = if (gameInProgress) R.string.online_game_in_progress else R.string.online_finished_game
    setText(value)
}

@BindingAdapter("app:online_game_created_by")
fun TextView.setGameCreator(creator: String) {
    val value = context.getString(R.string.online_game_created_by, creator)
    text = value
}

