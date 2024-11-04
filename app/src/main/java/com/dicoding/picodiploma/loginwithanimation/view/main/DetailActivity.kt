package com.dicoding.picodiploma.loginwithanimation.view.main

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.data.Result
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getStringExtra(EXTRA_ID)

        setupView(id.orEmpty())
    }

    private fun setupView(id: String) {
        lifecycleScope.launch {
            viewModel.getDetailStorybyId(id).observe(this@DetailActivity) { result ->
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }
                    is Result.Error -> {
                        with(binding) {
                            isLoading(false)
                            tvDetailName.text = result.error
                            tvDetailDescription.visibility = View.GONE
                            ivDetailPhoto.visibility = View.GONE
                        }
                    }
                    is Result.Success -> {
//                        with(binding) {
//                            isLoading(false)
//                            tvDetailName.text = result.data.story.name
//                            tvDetailDescription.text = result.data.story.description
//                            Glide.with(this@DetailActivity)
//                                .load(result.data.story.photoUrl)
//                                .into(ivDetailPhoto)
//                        }
                        isLoading(false)
                        setContent(result.data.story.name, result.data.story.description, result.data.story.photoUrl)

                        supportActionBar?.title = result.data.story.name + " Story"
                    }
                }
            }
        }
    }

    private fun setContent(name: String, description: String, photoUri: String) {
        with(binding) {
            tvDetailName.text = name
            tvDetailDescription.text = description
            Glide.with(this@DetailActivity)
                .load(photoUri)
                .into(ivDetailPhoto)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressCircular.visibility = View.VISIBLE
        } else {
            binding.progressCircular.visibility = View.GONE
        }
    }
}