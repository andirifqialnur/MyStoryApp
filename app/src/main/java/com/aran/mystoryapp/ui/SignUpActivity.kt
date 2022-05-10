package com.aran.mystoryapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import com.aran.mystoryapp.R
import com.aran.mystoryapp.api.ApiConfig
import com.aran.mystoryapp.custom.EmailCustom
import com.aran.mystoryapp.custom.PassCustom
import com.aran.mystoryapp.databinding.ActivitySignUpBinding
import com.aran.mystoryapp.response.Responses
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var emailCustom: EmailCustom
    private lateinit var passCustom: PassCustom
    private lateinit var btnSignUp : AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        playAnimation()

        emailCustom = binding.email
        passCustom = binding.pass
        btnSignUp = binding.btnSignUp

        nameValidate()

        binding.btnSignUp.setOnClickListener {
            val name = binding.name.text.toString()
            val email = binding.email.text.toString()
            val pass = binding.pass.text.toString()

            signUp(name, email, pass)
        }

        binding.toSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
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
                    val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
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

    companion object {
        private const val TAG = "Register Activity"
    }

    private fun setLoginButton() {
        val email = emailCustom.text.toString()
        if (passCustom.length() >= 8 && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            btnSignUp.isEnabled =true
        }
    }

}