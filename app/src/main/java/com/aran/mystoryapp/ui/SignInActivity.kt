package com.aran.mystoryapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.aran.mystoryapp.R
import com.aran.mystoryapp.api.ApiConfig
import com.aran.mystoryapp.custom.EmailCustom
import com.aran.mystoryapp.custom.PassCustom
import com.aran.mystoryapp.databinding.ActivitySignInBinding
import com.aran.mystoryapp.helper.SharedViewModel
import com.aran.mystoryapp.helper.UserPreference
import com.aran.mystoryapp.helper.ViewModelFactory
import com.aran.mystoryapp.model.UserModel
import com.aran.mystoryapp.response.SignInResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SignInActivity : AppCompatActivity() {

    private lateinit var mainViewModel: SharedViewModel
    private lateinit var binding: ActivitySignInBinding

    private lateinit var emailCustom: EmailCustom
    private lateinit var passCustom: PassCustom
    private lateinit var btnSignIn : AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupViewModel()

        playAnimation()

        emailCustom = binding.email
        passCustom = binding.pass
        btnSignIn = binding.btnSignIn

        binding.btnSignIn.setOnClickListener {
            val email = binding.email.text.toString()
            val pass = binding.pass.text.toString()

            signIn(email, pass)
        }

        binding.toSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        emailCustom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setLoginButton()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        passCustom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setLoginButton()
            }

            override fun afterTextChanged(p0: Editable?) {

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

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.txtTitle, View.ALPHA, 1f).setDuration(300)
        val title2 = ObjectAnimator.ofFloat(binding.txtTitle2, View.ALPHA, 1f).setDuration(300)
        val description = ObjectAnimator.ofFloat(binding.description, View.ALPHA, 1f).setDuration(300)
        val description2 = ObjectAnimator.ofFloat(binding.description2, View.ALPHA, 1f).setDuration(300)
        val edittext1 = ObjectAnimator.ofFloat(binding.edittext1, View.ALPHA, 1f).setDuration(300)
        val edittext2 = ObjectAnimator.ofFloat(binding.edittext2, View.ALPHA, 1f).setDuration(300)
        val checkBox = ObjectAnimator.ofFloat(binding.checkBox, View.ALPHA, 1f).setDuration(300)
        val btnSignIn = ObjectAnimator.ofFloat(binding.btnSignIn, View.ALPHA, 1f).setDuration(300)
        val descRegis = ObjectAnimator.ofFloat(binding.descRegis, View.ALPHA, 1f).setDuration(300)
        val toSignUp = ObjectAnimator.ofFloat(binding.toSignUp, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(title,
                    title2,
                    description,
                    description2,
                    edittext1,
                    edittext2,
                    checkBox,
                    btnSignIn,
                    descRegis,
                    toSignUp)
            start()
        }
    }

    private fun signIn(email: String, pass: String) {
        showLoading(true)

        val client = ApiConfig.getApiService().login(email, pass)
        client.enqueue(object: Callback<SignInResponse> {
            override fun onResponse(call: Call<SignInResponse>, response: Response<SignInResponse>) {
                showLoading(false)
                val responseBody = response.body()
                Log.d(TAG, "onResponse: $responseBody")
                if (response.isSuccessful && responseBody?.message == "success") {
                    mainViewModel.saveUser(UserModel(responseBody.loginResult.token, true))
                    Toast.makeText(this@SignInActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                } else {
                    Log.e(TAG, "onFailure1: ${response.message()}")
                    Toast.makeText(this@SignInActivity, getString(R.string.login_fail), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure2: ${t.message}")
                Toast.makeText(this@SignInActivity, getString(R.string.login_fail), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setLoginButton() {
        val email = emailCustom.text.toString()
        if (passCustom.length() >= 8 && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            btnSignIn.isEnabled =true
        }
    }

    companion object {
        private const val TAG = "Main Activity"
    }
}