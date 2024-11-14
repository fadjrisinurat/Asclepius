package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val confidence = intent.getFloatExtra(EXTRA_CONFIDENCE, 0f)
        val errorMessage = intent.getStringExtra(EXTRA_ERROR_MESSAGE)
        val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)
        val label = intent.getStringExtra(EXTRA_LABEL) ?: "Unknown"

        imageUriString?.let {
            val imageUri = Uri.parse(it)
            binding.resultImage.setImageURI(imageUri)
        } ?: run {

            binding.resultImage.setImageResource(R.drawable.ic_place_holder)
        }
        if (errorMessage != null) {
            binding.resultText.text = "Error processing the image: $errorMessage"
        } else {
            val resultText = "The Result Are: \n$label: ${"%.2f".format(confidence * 100)}%"
            binding.resultText.text = resultText
        }
    }

    companion object {

        const val EXTRA_CONFIDENCE = "extra_confidence"
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_ERROR_MESSAGE = "extra_error_message"
        const val EXTRA_LABEL = "extra_label"
    }
}

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.