package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.data.Result
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.view.AuthViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.login.LoginViewModel
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    private val viewmodel by viewModels<LoginViewModel> {
        AuthViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isLoading(false)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val name = binding.edRegisterName.text.toString()

            isLoading(true)
            lifecycleScope.launch {
                viewmodel.register(name, email, password).observe(this@SignupActivity) { result ->
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                            Log.d("SignupActivity", "Loading")
                        }
                        is Result.Success -> {
                            isLoading(false)
                            Log.d("SignupActivity", "Success")
                            Toast.makeText(this@SignupActivity, "Account $name Created", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@SignupActivity, WelcomeActivity::class.java)
                            startActivity(intent)
                        }
                        is Result.Error -> {
                            isLoading(false)
                            Log.d("SignupActivity", "Error")
                            AlertDialog.Builder(this@SignupActivity).apply {
                                setTitle("Error")
                                setMessage("Registration failed : ${result.error}")
                                setPositiveButton("OK") { _, _ -> }
                                create()
                                show()
                            }

                            with(binding) {
                                edRegisterName.setText("")
                                edRegisterEmail.setText("")
                                edRegisterPassword.setText("")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressIndicator.visibility = View.VISIBLE
        } else {
            binding.progressIndicator.visibility = View.GONE
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }
}