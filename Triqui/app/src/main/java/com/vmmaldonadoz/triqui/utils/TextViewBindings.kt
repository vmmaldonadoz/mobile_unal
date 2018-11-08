package com.vmmaldonadoz.triqui.utils

import android.databinding.BindingAdapter
import android.widget.TextView
import com.vmmaldonadoz.triqui.R

@BindingAdapter("app:online_game_state")
fun TextView.setGameState(finishedGame: Boolean) {
    val value = if (finishedGame) R.string.online_finished_game else R.string.online_game_in_progress
    setText(value)
}

@BindingAdapter("app:online_game_created_by")
fun TextView.setGameCreator(creator: String) {
    val value = context.getString(R.string.online_game_created_by, creator)
    text = value
}

