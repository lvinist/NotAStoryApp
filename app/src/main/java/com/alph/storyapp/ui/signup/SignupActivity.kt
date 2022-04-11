package com.alph.storyapp.ui.signup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alph.storyapp.databinding.ActivitySignupBinding
import com.alph.storyapp.ui.login.LoginActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.tvSigninIntent.setOnClickListener{
            val signinIntent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(signinIntent)
        }
    }
}