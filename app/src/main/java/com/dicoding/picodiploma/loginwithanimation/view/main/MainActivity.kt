package com.dicoding.picodiploma.loginwithanimation.view.main

import StoryAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.data.result.Result
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.detail.DetailStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
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
        }

        setupView()
        setupAction()
        setupRecyclerView()
        observeStories()
        observeUserSession()
        playButtonAnimation()
    }

    private fun setupRecyclerView() {
        binding.storyRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeStories() {
        viewModel.getSession().observe(this) { user ->
            Log.d("MainActivity", "Token retrieved in MainActivity: ${user.token}")
            if (user.isLogin) {
                val token = user.token
                viewModel.getStories().observe(this) { result ->
                    when (result) {
                        is Result.Success -> {
                            showLoading(false)
                            val stories = result.data.listStory
                            binding.storyRecyclerView.adapter = StoryAdapter(stories) { story ->
                                Log.d("MainActivity", "Navigating with Story ID: ${story.id}, Token: $token")
                                val intent = Intent(this, DetailStoryActivity::class.java)
                                intent.putExtra("STORY_ID", story.id)
                                intent.putExtra("TOKEN", token)
                                startActivity(intent)
                            }
                        }
                        is Result.Error -> {
                            showLoading(false)
                            showAlertDialog("Error", "Gagal memuat data: ${result.error.message}")
                        }
                        is Result.Loading -> {
                            showLoading(true)
                        }
                    }
                }
            } else {
                // Jika user tidak login
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }



    private fun observeUserSession() {
        viewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                binding.txProfile.text = "Hi! ${user.email}" // Ganti `email` dengan nama jika tersedia
            } else {
                binding.txProfile.text = "Hi! Guest"
            }
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK", null)
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }
    }

    // Menambahkan animasi pada tombol logout saat tombol ditekan
    private fun playButtonAnimation() {
        val scaleX = ObjectAnimator.ofFloat(binding.logoutButton, "scaleX", 1f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.logoutButton, "scaleY", 1f, 1.2f, 1f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.duration = 300 // Durasi animasi
        binding.logoutButton.setOnClickListener {
            animatorSet.start() // Menjalankan animasi saat tombol di-klik
            viewModel.logout() // Logout saat tombol diklik
        }
    }
}