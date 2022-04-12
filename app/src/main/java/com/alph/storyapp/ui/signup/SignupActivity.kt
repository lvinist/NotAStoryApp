package com.alph.storyapp.ui.signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alph.storyapp.R
import com.alph.storyapp.api.ApiConfig
import com.alph.storyapp.data.FileUploadResponse
import com.alph.storyapp.data.Register
import com.alph.storyapp.databinding.ActivitySignupBinding
import com.alph.storyapp.ui.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.btSignup.isEnabled = binding.edtPassword.text?.length!! >= 6
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.btSignup.isEnabled = binding.edtPassword.text?.length!! >= 6
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.btSignup.isEnabled = binding.edtPassword.text?.length!! >= 6
            }

        })

        binding.btSignup.setOnClickListener {registerUser()}
    }

    private fun registerUser() {
        val email = binding.edtEmail.text.toString().trim()
        val name = binding.edtName.text.toString().trim()
        val password = binding.edtPassword.toString().trim()

        binding.pbSignup.visibility = View.VISIBLE
        ApiConfig().getApiService().registerUser(Register(name, email, password))
            .enqueue(object: Callback<FileUploadResponse> {
                override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                    binding.pbSignup.visibility = View.INVISIBLE
                    Log.d("failure: ", t.message.toString())
                }
                override fun onResponse(
                    call: Call<FileUploadResponse>,
                    response: Response<FileUploadResponse>
                ) {
                    if (response.code() == 201) {
                        binding.pbSignup.visibility = View.INVISIBLE
                        Toast.makeText(applicationContext, getString(R.string.user_created), Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        binding.pbSignup.visibility = View.INVISIBLE
                        Toast.makeText(applicationContext, getString(R.string.invalid_input), Toast.LENGTH_SHORT).show()
                    }
                }

            })
    }
}