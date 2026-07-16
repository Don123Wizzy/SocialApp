package com.example.socialapp.feature_socialApp.feature_imageEditActivity.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.socialapp.adapters.UserSelectedImageAdapter
import com.example.socialapp.databinding.ActivityViewpager2Binding
import com.example.socialapp.feature_socialApp.feature_commentActivity.presentation.CommentActivityScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ImageEditActivityScreen : AppCompatActivity() {
    private lateinit var binding: ActivityViewpager2Binding
    private val viewModel: ImageEditActivityViewModel by viewModels()
    private var adapter = UserSelectedImageAdapter()
    private val newlyAddedImageUri = mutableListOf<Uri>()


    // GalleryImagePickerLauncher
    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents())
        { uris ->
            if (uris.isNotEmpty()) {
                newlyAddedImageUri.addAll(uris) // This to overt the original List to MutableList Type
                viewModel.onEvents(ImageEditActivityEvents.AddedImages(newlyAddedImageUri)) //Instead of List type, a MutableList type is passed
                // Also when the galleryImagePicker is launched the user select images, when the done button is liked
            }                                                                               // The viewModel.onEvents(ImageEditActivityEvents.AddedImages(newlyAddedImageUri) is ran and selectedImageUri are sent to the viewModel and further encapsulated so that it be collected in the ImageEditActivityScreen
        }

    //EditScreenLauncher
    private val editScreenLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val editedUri = result.uriContent
            val id = viewModel.uiState.value.currentEditImageId
            editedUri?.let { uri ->
                viewModel.onEvents(
                    ImageEditActivityEvents.EditedImage(
                        editedUri,
                        id
                    )
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityViewpager2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val viewPager = binding.viewpager2
        viewPager.adapter = adapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL


        //The comment activity sent images through intent to this current activity
        val receivedUris = intent.getParcelableArrayListExtra<Uri>("listOfSelectedUris")

        receivedUris?.let {
            viewModel.onEvents(ImageEditActivityEvents.ReceivedImagesFromCommentActivity(it.toMutableList()))

        }

        binding.addButton.setOnClickListener {
            viewModel.onEvents(ImageEditActivityEvents.OneTime.GalleryImageOneTimeEvent)
        }
        binding.editButton.setOnClickListener {

            val currentPageIndex =
                viewPager.currentItem // Get the index of the currently shown image on the viewPager

            val currentEditableOnScreen =
                viewModel.uiState.value.editableImages //with the index of the displayed image on the viewPager,
            // i obtained the Uri of that index from the the single source of truth(viewModel)

            val currentEditableOnScreenPosition = currentEditableOnScreen[currentPageIndex]

            viewModel.onEvents(
                ImageEditActivityEvents.OneTime.EditButton(
                    currentEditableOnScreenPosition
                )
            )
        }

        binding.deleteButton.setOnClickListener {
            val currentPageIndex = viewPager.currentItem
            val currentEditableOnScreen = viewModel.uiState.value.editableImages
            val currentDeletableOnScreenPosition = currentEditableOnScreen[currentPageIndex]
            viewModel.onEvents(
                ImageEditActivityEvents.OneTime.DeleteButton(
                    currentDeletableOnScreenPosition
                )
            )

        }
        binding.cancelButton.setOnClickListener {
            viewModel.onEvents(ImageEditActivityEvents.OneTime.CloseActivity)
        }
        binding.btnNext.setOnClickListener {

            viewModel.onEvents(ImageEditActivityEvents.OneTime.Next)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.oneTimeEvents.collect { events ->

                        when (events) {
                            is ImageEditActivityEvents.OneTime.CloseActivity -> {
                                finish()
                            }

                            is ImageEditActivityEvents.OneTime.EditButton -> {
                                editScreenLauncher.launch(
                                    CropImageContractOptions(
                                        events.editableImage.uri,
                                        CropImageOptions()
                                    )
                                )
                            }

                            is ImageEditActivityEvents.OneTime.GalleryImageOneTimeEvent -> {
                                imageLauncher.launch("image/*")

                            }

                            is ImageEditActivityEvents.OneTime.Next -> {

                                viewModel.uiState.value.uploadedUrls.let {

                                    val uploadedArrayList = ArrayList(it)
                                    val intent = Intent(
                                        this@ImageEditActivityScreen,
                                        CommentActivityScreen::class.java
                                    )
                                    intent.putStringArrayListExtra(
                                        "uploaded_urls",
                                        uploadedArrayList
                                    )
                                    startActivity(intent)

                                }

                            }

                            else -> {
                            }

                        }

                    }
                }
                launch {
                    viewModel.uiState.collect { state ->
                        adapter.submitList(state.editableImages)
                        Log.d("ImageEdit", "submitList size = ${state.editableImages.size}")

                    }

                }


            }
        }

    }
}