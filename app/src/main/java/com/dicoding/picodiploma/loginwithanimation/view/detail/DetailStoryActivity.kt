package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.result.Result
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailStoryBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private val viewModel: DetailViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra("STORY_ID")
        val token = intent.getStringExtra("TOKEN")

        Log.d("DetailStoryActivity", "Received STORY_ID: $storyId")
        Log.d("DetailStoryActivity", "Received TOKEN: $token")

        if (storyId != null && token != null) {
            observeStoryDetail(storyId, token)
        } else {
            showError("Invalid story data")
        }
    }


    private fun observeStoryDetail(storyId: String, token: String) {
        viewModel.getStoryDetail(storyId, token).observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    val story = result.data.story
                    binding.tvDetailName.text = story.name
                    binding.tvDetailDescription.text = story.description
                    Glide.with(this)
                        .load(story.photoUrl)
                        .placeholder(R.drawable.no_image)
                        .into(binding.ivDetailPhoto)
                }
                is Result.Error -> {
                    showLoading(false)
                    showError(result.error.message ?: "Failed to load story details")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        binding.tvDetailName.text = getString(R.string.error)
        binding.tvDetailDescription.text = message
    }
}
