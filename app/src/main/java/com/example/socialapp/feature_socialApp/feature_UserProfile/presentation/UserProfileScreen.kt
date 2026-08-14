package com.example.socialapp.feature_socialApp.feature_UserProfile.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.contentValuesOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.socialapp.R
import com.example.socialapp.databinding.BottomSheetDialogBinding
import com.example.socialapp.databinding.ProfileUserBinding
import com.example.socialapp.feature_socialApp.feature_editProfile.presentation.EditProfileScreen
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserProfileScreen : AppCompatActivity() {
    private lateinit var binding: ProfileUserBinding
    private val viewModel: UserProfileViewModel by viewModels()

    private val galleryImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                Glide.with(this).load(it).into(binding.imProfileImage2)
            }
        }

    private var createImageUri: Uri? = null
    private val cameraImageLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                Glide.with(this).load(createImageUri).into(binding.imProfileImage2)
            }

        }

    private fun createImageUri(): Uri? {
        val contentValues = contentValuesOf(
            MediaStore.Images.Media.DISPLAY_NAME to "profile_${System.currentTimeMillis()}.jpg",
            MediaStore.Images.Media.MIME_TYPE to "Images/jpeg"

        )
        return contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ProfileUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.tvEditPicture.setOnClickListener {
            viewModel.onEvents(UserProfileEvents.EditUserVisualDetailsButton)
        }

        binding.imEditProfile.setOnClickListener {
            viewModel.onEvents(UserProfileEvents.StartEditActivity)
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.events.collect { userProfileEvents ->
                        when (userProfileEvents) {
                            is UserProfileEvents.EditUserWrittenDetailsButton -> {

                            }

                            is UserProfileEvents.EditUserVisualDetailsButton -> {
                                createBottomSheet()
                            }
                            is UserProfileEvents.FetchProfileFailedError -> {
                                Toast.makeText(this@UserProfileScreen, userProfileEvents.errorMessage, Toast.LENGTH_SHORT).show()
                            }
                            is UserProfileEvents.StartEditActivity -> {
                                val intent = Intent(this@UserProfileScreen, EditProfileScreen::class.java)
                                startActivity(intent)
                            }
                        }

                    }
                }
                launch {
                    viewModel.uiState.collect { userProfileState ->
                        Glide.with(binding.root).load(userProfileState.profileImage)
                            .placeholder(R.drawable.ic_placeholder).into(binding.imProfileImage2)
                        binding.tvName.text = userProfileState.userName
                        binding.tvShortBio.text = userProfileState.bio
                        binding.tvJobTitle.text = userProfileState.jobTitle
                    }
                }
            }
        }
    }

    private fun createBottomSheet() {
        val bottomSheetBinding = BottomSheetDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this, R.style.LinkedInBottomSheetTheme)
        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()

        bottomSheetBinding.btnTakeAPhoto.setOnClickListener {
            dialog.dismiss()
            createImageUri = createImageUri()
            createImageUri?.let {
                cameraImageLauncher.launch(it)
            }

        }
        bottomSheetBinding.btnChooseFromGallery.setOnClickListener {
            dialog.dismiss()
            galleryImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }
    }
}