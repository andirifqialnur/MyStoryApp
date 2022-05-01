package com.aran.mystoryapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.aran.mystoryapp.R
import com.aran.mystoryapp.api.ApiConfig
import com.aran.mystoryapp.databinding.ActivitySignUpBinding
import com.aran.mystoryapp.helper.SharedViewModel
import com.aran.mystoryapp.helper.UserPreference
import com.aran.mystoryapp.helper.ViewModelFactory
import com.aran.mystoryapp.response.Responses
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var mainViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupViewModel()

        playAnimation()

        nameValidate()
        emailValidate()
        passwordValidate()

        binding.btnSignUp.setOnClickListener {
            val name = binding.name.text.toString()
            val email = binding.email.text.toString()
            val pass = binding.pass.text.toString()

            signUp(name, email, pass)
        }

        binding.toSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.txtTitle, View.ALPHA, 1f).setDuration(300)
        val title2 = ObjectAnimator.ofFloat(binding.txtTitle2, View.ALPHA, 1f).setDuration(300)
        val description = ObjectAnimator.ofFloat(binding.description, View.ALPHA, 1f).setDuration(300)
        val description2 = ObjectAnimator.ofFloat(binding.description2, View.ALPHA, 1f).setDuration(300)
        val edittext1 = ObjectAnimator.ofFloat(binding.edittext1, View.ALPHA, 1f).setDuration(300)
        val edittext2 = ObjectAnimator.ofFloat(binding.edittext2, View.ALPHA, 1f).setDuration(300)
        val edittext3 = ObjectAnimator.ofFloat(binding.edittext3, View.ALPHA, 1f).setDuration(300)
        val checkBox = ObjectAnimator.ofFloat(binding.checkBox, View.ALPHA, 1f).setDuration(300)
        val btnSignIn = ObjectAnimator.ofFloat(binding.btnSignUp, View.ALPHA, 1f).setDuration(300)
        val descRegis = ObjectAnimator.ofFloat(binding.descRegis, View.ALPHA, 1f).setDuration(300)
        val toSignUp = ObjectAnimator.ofFloat(binding.toSignIn, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(title,
                title2,
                description,
                description2,
                edittext1,
                edittext2,
                edittext3,
                checkBox,
                btnSignIn,
                descRegis,
                toSignUp)
            start()
        }
    }

    private fun signUp(name: String, email: String, pass: String) {
        showLoading(true)

        val client = ApiConfig.getApiService().createAccount(name, email, pass)
        client.enqueue(object: Callback<Responses> {
            override fun onResponse(
                call: Call<Responses>,
                response: Response<Responses>
            ) {
                showLoading(false)
                val responseBody = response.body()
                Log.d(TAG, "onResponse: $responseBody")
                if(response.isSuccessful && responseBody?.message == "User created") {
                    Toast.makeText(this@SignUpActivity, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.e(TAG, "onFailure1: ${response.message()}")
                    Toast.makeText(this@SignUpActivity, getString(R.string.register_fail), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Responses>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure2: ${t.message}")
                Toast.makeText(this@SignUpActivity, getString(R.string.register_fail), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[SharedViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            if(user.isLogin) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun nameValidate() {
        binding.name.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.edittext1.helperText = validName()
            }
        }
    }

    private fun validName(): String? {
        val nameValue = binding.name.text.toString()
        if (nameValue.length > 20) {
            binding.edittext1.error = "Too long!"
        } else {
            binding.edittext1.error = null
        }
        return null
    }

    private fun emailValidate() {
        binding.email.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.edittext2.helperText = validEmail()
            }
        }
    }

    private fun validEmail(): String? {
        val emailValue = binding.email.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            return "Invalid Email Address"
        }
        return null
    }

    private fun passwordValidate() {
        binding.pass.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.edittext3.helperText = validPass()
            }
        }
    }

    private fun validPass(): String? {
        val passValue = binding.pass.text.toString()
        if (passValue.length < 8) {
            return "Minimum 8 Character Password"
        }
        if (!passValue.matches(".*[A-Z].*".toRegex())) {
            return "Must Contain 1 Upper-case Character"
        }
        if (!passValue.matches(".*[a-z].*".toRegex())) {
            return "Must Contain 1 Lower-case Character"
        }
        if (!passValue.matches(".*[A-Z].*".toRegex())) {
            return "Must Contain 1 Upper-case Character"
        }
        if (!passValue.matches(".*[@#$%^?&*].*".toRegex())) {
            return "Must Contain 1 Special Character (@#\$%^?&*)"
        }
        return null
    }

    companion object {
        private const val TAG = "Register Activity"
    }

}