package com.vmmaldonadoz.triqui.activities

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.vmmaldonadoz.triqui.R
import com.vmmaldonadoz.triqui.constants.PREFERENCE_NICKNAME_KEY
import com.vmmaldonadoz.triqui.databinding.ActivityGameMenuBinding
import com.vmmaldonadoz.triqui.utils.isNotNullOrBlank

class GameMenuActivity : AppCompatActivity() {

    private val binding: ActivityGameMenuBinding by lazy {
        DataBindingUtil.setContentView<ActivityGameMenuBinding>(this,
                R.layout.activity_game_menu)
    }

    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root

        bindSavedNickname()
        bindClickListeners()
    }

    private fun bindClickListeners() {
        binding.buttonPlayOffline.setOnClickListener {
            if (isValidNickname()) {
                saveNickname()
                startOfflineGameActivity()
            }
        }
        binding.buttonPlayOnline.setOnClickListener {
            if (isValidNickname()) {
                saveNickname()
                startOnlineGameActivity()
            }
        }
    }

    private fun isValidNickname(): Boolean {
        return binding.editTextNickname.text.isNotNullOrBlank()
    }

    private fun saveNickname() {
        preferences.edit().putString(PREFERENCE_NICKNAME_KEY, binding.editTextNickname.text.toString()).apply()
    }

    private fun startOnlineGameActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun startOfflineGameActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun bindSavedNickname() {

        val savedNickname = preferences.getString(PREFERENCE_NICKNAME_KEY, "").orEmpty()
        binding.editTextNickname.setText(savedNickname)
    }
}
