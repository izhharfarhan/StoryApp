package com.dicoding.picodiploma.loginwithanimation.view.addstory

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.util.getImageUri
import com.dicoding.picodiploma.loginwithanimation.view.util.uriToFile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var viewModel: AddStoryViewModel
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[AddStoryViewModel::class.java]

        setupUI()
    }

    private fun setupUI() {
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.buttonAdd.setOnClickListener { uploadImage() }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPreviewImage.setImageURI(it)
        }
    }

    private fun uploadImage() {
        val description = binding.edAddDescription.text.toString().trim()

        // Validasi input
        when {
            description.isEmpty() && currentImageUri == null -> {
                showToast(getString(R.string.error_empty_both))
                return
            }
            description.isEmpty() -> {
                showToast(getString(R.string.error_empty_description))
                return
            }
            currentImageUri == null -> {
                showToast(getString(R.string.error_empty_image))
                return
            }
        }

        // Proses upload
        val imageFile = uriToFile(currentImageUri!!, this)
        val descriptionRequest = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart = MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)

        // Ambil token dari ViewModel
        viewModel.getSession().observe(this) { user ->
            val token = user.token
            if (token.isNotEmpty()) {
                lifecycleScope.launch {
                    viewModel.addStory(token, imageMultipart, descriptionRequest).observe(this@AddStoryActivity) { result ->
                        when (result) {
                            is com.dicoding.picodiploma.loginwithanimation.data.result.Result.Loading -> showLoading(true)
                            is com.dicoding.picodiploma.loginwithanimation.data.result.Result.Success -> {
                                showLoading(false)
                                showToast(getString(R.string.story_added_success))
                                navigateToMainActivity()
                            }
                            is com.dicoding.picodiploma.loginwithanimation.data.result.Result.Error -> {
                                showLoading(false)
                                showToast(result.error.message ?: getString(R.string.upload_failed))
                            }
                        }
                    }
                }
            } else {
                showToast(getString(R.string.token_missing))
            }
        }
    }


    private fun navigateToMainActivity() {
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
