package com.example.socialapp.feature_socialApp.feature_completeProfile.presentation

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.contentValuesOf
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.socialapp.MainActivity
import com.example.socialapp.R
import com.example.socialapp.databinding.ActivityCompleteProfileScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CompleteProfileScreen() : AppCompatActivity() {
    private lateinit var binding: ActivityCompleteProfileScreenBinding
    private val viewModel: CompleteProfileViewModel by viewModels()


    private val pickingImageFromGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Glide.with(binding.root).load(uri).into(binding.ivProfileImage)
                viewModel.onEvents(CompleteProfileEvents.ProfileImageSelected(uri))
            }
        }


    private var createImageUri: Uri? = null
    private val cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                createImageUri?.let {
                    Glide.with(binding.root).load(createImageUri).into(binding.ivProfileImage)
                    viewModel.onEvents(CompleteProfileEvents.ProfileImageSelected(it))
                }

            }
        }


    // Creates a MediaStore entry and provides metadata for the image before it is captured.
// Returns the Uri (location in MediaStore) where the camera will save the captured image.
    // it just basically prepares the ground for the image that will eventually be captured by the user
    private fun createImageUri(): Uri {

        val contentValues = contentValuesOf(
            MediaStore.Images.Media.DISPLAY_NAME to "profile_${System.currentTimeMillis()}.jpg",
            MediaStore.Images.Media.MIME_TYPE to "image/jpeg"
        )

        return contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: throw IllegalStateException("Failed to create image Uri")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCompleteProfileScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Obtained from the logic screen
        //user is navigated here and immediately see his/her google image picture
        val googlePhotoUrl = intent.getStringExtra("googleProfilePicture")

        Log.d("PHOTO_TEST", "URL before Glide = $googlePhotoUrl")

        Glide.with(this)
            .load(googlePhotoUrl)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(binding.ivProfileImage)


        //receiving result from ImagePickerBottomSheet
        //we use a supportFragmentManager because this is an activity
        supportFragmentManager.setFragmentResultListener("MyResult", this) { _, bundle ->
            val options = bundle.getString("options")
            when (options) {
                "camera" -> {
                    createImageUri = createImageUri()
                    createImageUri?.let {
                        cameraResultLauncher.launch(it)
                    }

                }

                "gallery" -> {
                    pickingImageFromGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

                }
            }

        }




        binding.etJobTitle.addTextChangedListener {
            viewModel.onEvents(CompleteProfileEvents.JobTitle(it.toString()))
        }
        binding.etBio.addTextChangedListener {
            viewModel.onEvents(CompleteProfileEvents.Bio(it.toString()))
        }

        binding.ivProfileImage.setOnClickListener {
            ImagePickerBottomSheet().show(
                supportFragmentManager,
                "ImagePickerBottomSheet"
            ) //This tells the supportFragmentManager, Please add this fragment to the Activity and display it.
        }

        binding.btnContinue.setOnClickListener {
            viewModel.onEvents(CompleteProfileEvents.Continue)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        Glide.with(binding.root).load(state.uri).into(binding.ivProfileImage)

                        if (binding.etJobTitle.text.toString() != state.jobTitle) {
                            binding.etJobTitle.setText(state.jobTitle)
                        }

                        if (binding.etBio.text.toString() != state.bio) {
                            binding.etJobTitle.setText(state.bio)
                        }
                    }

                }
                launch {
                    viewModel.oneTimeEvents.collect { oneTimeEvents ->
                        when (oneTimeEvents) {
                            is CompleteProfileEvents.NavigateToMain -> {
                                val intent =
                                    Intent(this@CompleteProfileScreen, MainActivity::class.java)
                                startActivity(intent)
                            }

                            else -> {}
                        }

                    }

                }
            }
        }
    }
}