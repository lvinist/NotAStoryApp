package com.alph.storyapp.ui.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.alph.storyapp.data.Register
import com.alph.storyapp.databinding.ActivitySignupBinding
import com.alph.storyapp.utils.Constant.REGISTER_RESPONSE
import com.alph.storyapp.utils.utils.isValidEmail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    private val signupViewModel: SignupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setButtonShouldEnabled()
        setSubmitButton()
        observeResponse()
    }

    private fun setSubmitButton() {
        binding.btSignup.setOnClickListener {
            binding.pbSignup.visibility = View.VISIBLE

            val username = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            lifecycleScope.launch {
                signupViewModel.register(Register(username, email, password))
            }
        }
    }

    private fun setButtonShouldEnabled() {
        binding.apply {
            edtName.addTextChangedListener {
                setButtonEnabled()
            }
            edtEmail.addTextChangedListener {
                setButtonEnabled()
            }
            edtPassword.addTextChangedListener {
                setButtonEnabled()
            }
        }
    }

    private fun setButtonEnabled() {
        val username = binding.edtName.text.toString()
        val password = binding.edtPassword.text.toString()
        val email = binding.edtEmail.text.toString()
        val buttonShouldEnabled = (password
            .isNotEmpty() && password.length >= 6) && (email
            .isNotEmpty() && email.isValidEmail() && username.isNotEmpty())
        binding.btSignup.isEnabled = buttonShouldEnabled
    }

    private fun observeResponse() {
        signupViewModel.response.observe(this) {
            if (!it.error!!) {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK, Intent().putExtra(REGISTER_RESPONSE, it))
                finishAndRemoveTask()
            }
            if(it.error){
                setResult(RESULT_OK, Intent().putExtra(REGISTER_RESPONSE, it))
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                binding.pbSignup.visibility = View.GONE
            }
        }
    }
}