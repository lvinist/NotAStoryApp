package com.alph.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.alph.storyapp.R
import com.alph.storyapp.ui.main.MainActivity

class SplashScreen : AppCompatActivity() {

    companion object {
        private const val EXTRA_DURATION = 3000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        Handler().postDelayed({
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, EXTRA_DURATION)
    }
}