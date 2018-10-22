package com.vmmaldonadoz.triqui.activities

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.ListPreference
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import com.vmmaldonadoz.triqui.R

class PreferencesActivity : PreferenceActivity() {

    private val difficultyLevelKey = "difficulty_level"

    private val victoryMessageKey = "victory_message"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        bindDifficultyLevelPreference(preferences)
        bindHumanWinsPreference(preferences)
    }

    @SuppressLint("ApplySharedPref")
    private fun bindHumanWinsPreference(preferences: SharedPreferences) {
        val victoryMessagePreference = findPreference(victoryMessageKey) as EditTextPreference
        val victoryMessage = preferences.getString(victoryMessageKey, getString(R.string.result_human_wins))

        victoryMessagePreference.summary = victoryMessage
        victoryMessagePreference.setOnPreferenceChangeListener { preference, newValue ->
            victoryMessagePreference.summary = newValue.toString()

            val preferenceEditor = preferences.edit()
            preferenceEditor.putString(victoryMessageKey, newValue.toString())
            preferenceEditor.commit()

            true
        }
    }

    @SuppressLint("ApplySharedPref")
    private fun bindDifficultyLevelPreference(preferences: SharedPreferences) {
        val difficultyLevelPreference = findPreference(difficultyLevelKey) as ListPreference
        val difficulty = preferences.getString(difficultyLevelKey, getString(R.string.difficulty_expert))
        difficultyLevelPreference.summary = difficulty

        difficultyLevelPreference.setOnPreferenceChangeListener { _, newValue ->
            difficultyLevelPreference.summary = newValue as String

            val preferenceEditor = preferences.edit()
            preferenceEditor.putString(difficultyLevelKey, newValue.toString())
            preferenceEditor.commit()

            true
        }
    }
}
