package com.dicoding.asclepius.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.startActivity
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

            imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        val topResult = results?.firstOrNull()?.categories?.firstOrNull()
                        val label = topResult?.label ?: "Unknown"
                        val confidence = topResult?.score ?: 0f

                        moveToResult(confidence, label = label)
                    }

                    override fun onError(error: String) {
                        moveToResult(errorMessage = error)
                    }
                }
            )

            binding.galleryButton.setOnClickListener { startGallery() }
            binding.analyzeButton.setOnClickListener { analyzeImage() }
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

    private fun showImage() {
        // TODO: Menampilkan gambar sesuai Gallery yang dipilih.
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }


    private fun analyzeImage() {
        // TODO: Menganalisa gambar yang berhasil ditampilkan.
        if (currentImageUri == null) {
            showToast("Please select an image ")
            return
        }
        currentImageUri?.let {
            imageClassifierHelper.classifyStaticImage(it, contentResolver)
        }
    }



private fun moveToResult(confidence: Float = -1f, errorMessage: String? = null, label: String? = null) {
    val intent = Intent(this, ResultActivity::class.java).apply {
        putExtra(ResultActivity.EXTRA_CONFIDENCE, confidence)
        putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
        putExtra(ResultActivity.EXTRA_ERROR_MESSAGE, errorMessage)
        putExtra(ResultActivity.EXTRA_LABEL, label)
    }
    startActivity(intent)
}

private fun showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
private fun showLoading(isLoading: Boolean) {
    binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
}
}