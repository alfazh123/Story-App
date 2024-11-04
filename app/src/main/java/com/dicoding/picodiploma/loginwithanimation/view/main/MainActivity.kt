package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.Result
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.utils.ListStoriesAdapter
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch
import com.dicoding.picodiploma.loginwithanimation.view.AuthViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.login.LoginViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val authViewModel by viewModels<LoginViewModel> {
        AuthViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
            setupView()
            binding.textNameUser.text = user.name
            Toast.makeText(this, "Welcome ${user.name}", Toast.LENGTH_SHORT).show()
        }
        setupAction()
    }

    private fun setupView() {

        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStories.addItemDecoration(itemDecoration)

        lifecycleScope.launch {
            viewModel.getAllStories().observe(this@MainActivity) { result ->
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }
                    is Result.Error -> {
                        isLoading(false)
                        binding.textNameUser.text = result.error
                        Toast.makeText(this@MainActivity, result.error, Toast.LENGTH_SHORT).show()
                    }
                    is Result.Success -> {
                        isLoading(false)
                        val adapter = ListStoriesAdapter()
                        adapter.submitList(result.data.listStory)
                        binding.rvStories.adapter = adapter

                        adapter.setOnItemClickCallback(object : ListStoriesAdapter.OnItemClickback {
                            override fun onItemClicked(story: ListStoryItem) {
                                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                                intent.putExtra(DetailActivity.EXTRA_ID, story.id)
                                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    this@MainActivity,
                                    binding.rvStories,
                                    "sharedElementsName"
                                ).toBundle())

                            }

                        })
                    }
                }
            }
        }
    }

    private fun setupAction() {
        binding.fabAddStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.menu_logout -> {
                authViewModel.logout()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun isLoading(isloading: Boolean) {
        if (isloading) {
            binding.progressCircular.visibility = View.VISIBLE
        } else {
            binding.progressCircular.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.getAllStories()
        }
    }
}