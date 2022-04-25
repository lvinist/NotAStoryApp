package com.alph.storyapp.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alph.storyapp.data.FileUploadResponse
import com.alph.storyapp.data.Login
import com.alph.storyapp.databinding.ActivityLoginBinding
import com.alph.storyapp.ui.main.MainActivity
import com.alph.storyapp.ui.signup.SignupActivity
import com.alph.storyapp.utils.Constant.REGISTER_RESPONSE
import com.alph.storyapp.utils.utils.isValidEmail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels()

    private val launcherRegister =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val response = it.data?.getParcelableExtra<FileUploadResponse>(REGISTER_RESPONSE)
                Log.d("login", "$response")
                Toast.makeText(this, "${response?.message}", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        signupUser()
        observeLoginToken()
        observeLoginResponse()
        setEnableSignInButton()
        signinButtonClickListener()
    }

    private fun signupUser() {
        binding.tvSignupIntent.setOnClickListener{
            val signUpIntent = Intent(this@LoginActivity, SignupActivity::class.java)
            launcherRegister.launch(signUpIntent)
        }
    }

    private fun observeLoginResponse() {
        loginViewModel.loginResponse.observe(this) { loginResponse ->
            loginResponse.error.let {
                when (it) {
                    true -> {
                        Toast.makeText(this, loginResponse.message, Toast.LENGTH_SHORT).show()
                        binding.pbLogin.visibility = View.GONE
                    }
                    false -> {
                        startActivity(Intent(this, MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        })
                        finish()
                    }
                }
            }
        }
    }

    private fun observeLoginToken() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                loginViewModel.loginToken.collect {
                    if (it.isNotEmpty()) {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        })
                        finish()
                    }
                }
            }
        }
    }

    private fun signinButtonClickListener() {
        binding.btnSignin.setOnClickListener {
            binding.apply {
                tvPassword.setOnEditorActionListener { textView, actionId, _ ->
                    if (textView.text.isNotEmpty() && textView.error.isNullOrEmpty() && binding.tvEmail.error.isNullOrEmpty()) {
                        loginViewModel.login(
                            Login(
                                binding.tvEmail.text.toString(),
                                textView.text.toString()
                            )
                        )
                        textView.closeSoftKeyboard()
                    }
                    actionId == EditorInfo.IME_ACTION_DONE
                }
                binding.pbLogin.visibility = View.VISIBLE
            }
            loginViewModel.login(
                Login(
                    binding.tvEmail.text.toString(),
                    binding.tvPassword.text.toString()
                )
            )
        }
    }

    private fun View.closeSoftKeyboard() {
        val inputMethodManager =
            this.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
        this.clearFocus()
    }

    private fun setEnableSignInButton() {
        binding.tvPassword.addTextChangedListener {
            setButtonEnabled()
        }
    }

    private fun setButtonEnabled() {
        val password = binding.tvPassword.text
        val email = binding.tvEmail.text
        val buttonShouldEnabled = (password.toString()
            .isNotEmpty() && password.toString().length >= 6) && (email.toString()
            .isNotEmpty() && email.toString().isValidEmail())
        binding.btnSignin.isEnabled = buttonShouldEnabled
    }
}



