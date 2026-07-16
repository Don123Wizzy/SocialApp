package com.example.socialapp.feature_socialApp.feature_commentActivity.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialapp.MainActivity
import com.example.socialapp.adapters.PostPreviewAdapter
import com.example.socialapp.databinding.ActivityCommentBinding
import com.example.socialapp.feature_socialApp.feature_commentActivity.domain.use_case.CreatePostUseCase
import com.example.socialapp.feature_socialApp.feature_commentActivity.presentation.model.CreatePostRequest
//import com.example.socialapp.editPakage.ViewPager2Activity
import com.example.socialapp.feature_socialApp.feature_imageEditActivity.presentation.ImageEditActivityScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommentActivityScreen : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding
    private val viewModel: CommentActivityViewModel by viewModels()
    private var listOfReceivedUrlsFromCommentActivityScreen = listOf<String>()
    private var adapter = PostPreviewAdapter()


    val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                viewModel.onEvents(CommentActivityEvents.OnImageSelected(uris))
                Log.d("Flow", "1. Picked ${uris.size} images")

            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val postPreviewAdapter = binding.rcImageVisuals
        postPreviewAdapter.adapter = adapter
        postPreviewAdapter.layoutManager = LinearLayoutManager(this)





        listOfReceivedUrlsFromCommentActivityScreen =
            intent.getStringArrayListExtra("uploaded_urls") ?: emptyList()
        Log.d("omo", "${listOfReceivedUrlsFromCommentActivityScreen.size}")
        viewModel.onEvents(
            CommentActivityEvents.OnUploadedUrls(
                listOfReceivedUrlsFromCommentActivityScreen
            )
        )



        binding.etUserComment1.addTextChangedListener { shareIdeaMessage ->
            viewModel.onEvents(CommentActivityEvents.ShareIdea(shareIdeaMessage.toString()))
        }

        binding.btnCommentActivityRemoval.setOnClickListener {
            viewModel.onEvents(CommentActivityEvents.OneTime.CloseCommentActivity)
        }

        binding.btnCommentPost.setOnClickListener {
            viewModel.onEvents(CommentActivityEvents.OneTime.PostUserContent)

        }

        binding.imAddImageToPost.setOnClickListener {
            viewModel.onEvents(CommentActivityEvents.OneTime.GalleryImagePicker)
        }

        binding.imReEdit.setOnClickListener {
            viewModel.onEvents(CommentActivityEvents.OneTime.ReselectGalleryImagePicker)
        }


        //viewModel emitting one time events
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.oneTimeEvents.collect { event ->
                        when (event) {
                            is CommentActivityEvents.OneTime.CloseImageViewButtonIcon -> {
                                binding.rcImageVisuals.visibility = View.GONE
                                binding.imReEdit.visibility = View.GONE
                                binding.imCancel.visibility = View.GONE

                            }

                            is CommentActivityEvents.OneTime.NavigatingToEditingScreen -> {
                                val intent = Intent(
                                    this@CommentActivityScreen,
                                    ImageEditActivityScreen::class.java
                                )
                                //Log.d("CommentActivity", "About to start activity")
                                intent.putParcelableArrayListExtra(
                                    "listOfSelectedUris",
                                    ArrayList(event.receivedUris)
                                )
                                startActivity(intent)

                            }

                            is CommentActivityEvents.OneTime.ReselectGalleryImagePicker -> {

                            }

                            is CommentActivityEvents.OneTime.CloseCommentActivity -> {
                                finish()
                            }

                            is CommentActivityEvents.OneTime.PostUserContentSuccess -> {
                                Toast.makeText(this@CommentActivityScreen, event.message,Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@CommentActivityScreen, MainActivity::class.java )
                               intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                               startActivity(intent)
                                finish()
                            }
                            is CommentActivityEvents.OneTime.PostUserContentError -> {
                                Toast.makeText(this@CommentActivityScreen, event.error,Toast.LENGTH_SHORT).show()
                            }

                            is CommentActivityEvents.OneTime.GalleryImagePicker -> {
                                imageResultLauncher.launch("image/*")
                            }

                            else -> {

                            }

                        }
                    }
                }
                launch {
                    viewModel.state.collect { state ->
                        if (binding.etUserComment1.text.toString() != state.ideaMessage) {
                            binding.etUserComment1.setText(state.ideaMessage)
                        }
                        binding.btnCommentPost.isEnabled = state.ideaMessage.isNotEmpty()

                        adapter.submitList(state.uploadedUrls)
                        Log.d("omo", "${state.uploadedUrls.size}")


                    }
                }


            }


        }
    }
}