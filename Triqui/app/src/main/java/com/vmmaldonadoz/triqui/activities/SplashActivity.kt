package com.vmmaldonadoz.triqui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.vmmaldonadoz.triqui.R
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            startActivity(Intent(this, GameMenuActivity::class.java))
            finish()
        }, TimeUnit.SECONDS.toMillis(2))
    }
}
