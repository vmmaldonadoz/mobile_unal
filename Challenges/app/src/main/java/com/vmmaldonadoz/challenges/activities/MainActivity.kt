package com.vmmaldonadoz.challenges.activities

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.vmmaldonadoz.challenges.R
import com.vmmaldonadoz.challenges.challengeItem
import com.vmmaldonadoz.challenges.databinding.ActivityMainBinding
import com.vmmaldonadoz.challenges.models.Challenge
import com.vmmaldonadoz.challenges.utils.withModels

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this,
                R.layout.activity_main)
    }

    private val challenges by lazy {
        listOf(Challenge("8", getString(R.string.challenge_8)),
                Challenge("9", getString(R.string.challenge_9)),
                Challenge("10", getString(R.string.challenge_10)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root

        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        binding.recyclerView.withModels {
            challenges.forEach { challenge ->
                challengeItem {
                    id(challenge.id)
                    model(challenge)
                    onClick(View.OnClickListener { handleClick(challenge.id) })
                }
            }
        }
    }

    private fun handleClick(id: String) {
        val intent = when (id) {
            "8" -> Intent(this, SqliteChallengeActivity::class.java)
            "9" -> Intent(this, MapsActivity::class.java)
            else -> Intent(this, SqliteChallengeActivity::class.java)
        }
        startActivity(intent)
    }
}
