package com.alph.storyapp.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.alph.storyapp.R
import com.alph.storyapp.api.ApiConfig
import com.alph.storyapp.data.Login
import com.alph.storyapp.data.LoginResponse
import com.alph.storyapp.data.User
import com.alph.storyapp.databinding.ActivityLoginBinding
import com.alph.storyapp.storage.UserPreference
import com.alph.storyapp.ui.ViewModelFactory
import com.alph.storyapp.ui.main.MainActivity
import com.alph.storyapp.ui.signup.SignupActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupViewModel()

        binding.tvSignupIntent.setOnClickListener {
            val signUpIntent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(signUpIntent)
        }

        binding.tvPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.btnSignin.isEnabled = binding.tvPassword.text?.length!! >= 6
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.btnSignin.isEnabled = binding.tvPassword.text?.length!! >= 6
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.btnSignin.isEnabled = binding.tvPassword.text?.length!! >= 6
            }

        })

        binding.btnSignin.setOnClickListener { loginUser() }
    }

    private fun setupViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        loginViewModel = ViewModelProvider(
            this@LoginActivity,
            ViewModelFactory(pref)
        )[LoginViewModel::class.java]
    }

    private fun loginUser() {
        val email = binding.tvEmail.text.toString()
        val password = binding.tvPassword.text.toString().trim()

        binding.pbLogin.visibility = View.VISIBLE
        ApiConfig().getApiService().loginUser(Login(email, password))
            .enqueue(object : Callback<LoginResponse> {

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.d("failure: ", t.message.toString())
                }

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.code() == 200) {
                        val body = response.body()?.loginResult as User

                        loginViewModel.saveUser(User(body.userId, body.name, body.token, true))
                        binding.pbLogin.visibility = View.INVISIBLE

                        Toast.makeText(
                            applicationContext,
                            getString(R.string.success_login),
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } else {
                        binding.pbLogin.visibility = View.INVISIBLE

                        Toast.makeText(
                            applicationContext,
                            getString(R.string.fail_login),
                            Toast.LENGTH_SHORT
                        ).show()

                        Log.d(
                            LoginActivity::class.java.simpleName,
                            response.body()?.message.toString()
                        )
                    }
                }

            })


    }
}

class EmailEditText : AppCompatEditText {

    private var emailRegex: Regex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }


    private fun init() {

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    error = context.getString(R.string.err_empty_email)
                    requestFocus()
                } else if (!s.toString().matches(emailRegex)) {
                    error = context.getString(R.string.err_invalid_email)
                    requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //do nothing
            }
        })
    }
}

class PasswordEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }


    private fun init() {

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length < 6) {
                    error = "Password must be at least 6 character long"
                    requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //do nothing
            }
        })
    }
}
