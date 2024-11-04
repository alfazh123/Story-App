package com.dicoding.picodiploma.loginwithanimation.view.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.Result
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.utils.getImageUri
import com.dicoding.picodiploma.loginwithanimation.view.utils.reduceFileImage
import com.dicoding.picodiploma.loginwithanimation.view.utils.uriToFile
import kotlinx.coroutines.launch

class AddStoryActivity : AppCompatActivity() {

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

    private lateinit var binding: ActivityAddStoryBinding

    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isLoading(false)
        setupAction()
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

    private fun setupAction() {
        with(binding) {
            galleryButton.setOnClickListener {
                launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            cameraButton.setOnClickListener {
                currentImageUri = getImageUri(this@AddStoryActivity)
                launcherIntentCamera.launch(currentImageUri!!)
            }
            uploadButton.setOnClickListener {
                isLoading(true)
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.edtDescription.text.toString().trim()

            if (description.isEmpty()) {
                binding.edtDescription.error = getString(R.string.error_description_empty)
                return
            } else {
//                lifecycleScope.launch {
//                    viewModel.addStory(imageFile, description).observe(this@AddStoryActivity) { result ->
//                        when(result) {
//                            is Result.Loading -> {
//                                isLoading(true)
//                            }
//                            is Result.Error -> {
//                                isLoading(false)
//                                AlertDialog.Builder(this@AddStoryActivity).apply {
//                                    setTitle("Error")
//                                    setMessage("Failed Add Story : ${result.error}")
//                                    setPositiveButton("OK") { _, _ -> }
//                                    create()
//                                    show()
//                                }
//                            }
//                            is Result.Success -> {
//                                isLoading(false)
//                                val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
//                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                                startActivity(intent)
//                                Toast.makeText(this@AddStoryActivity, "Add Story Success", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//                }

                // with coroutine
                viewModel.addNewStory(imageFile, description).observe(this) { result ->
                    when(result) {
                            is Result.Loading -> {
                                isLoading(true)
                            }
                            is Result.Error -> {
                                isLoading(false)
                                AlertDialog.Builder(this).apply {
                                    setTitle("Error")
                                    setMessage("Failed Add Story : ${result.error}")
                                    setPositiveButton("OK") { _, _ -> }
                                    create()
                                    show()
                                }
                            }
                            is Result.Success -> {
                                isLoading(false)
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                Toast.makeText(this, "Add Story Success", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
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

    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressIndicator.visibility = View.VISIBLE
        } else {
            binding.progressIndicator.visibility = View.GONE
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }
}