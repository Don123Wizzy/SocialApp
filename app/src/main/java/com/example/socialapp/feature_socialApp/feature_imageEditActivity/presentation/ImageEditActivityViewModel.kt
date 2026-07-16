package com.example.socialapp.feature_socialApp.feature_imageEditActivity.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.feature_socialApp.feature_imageEditActivity.domain.use_case.UploadImagesUseCase
import com.example.socialapp.feature_socialApp.feature_imageEditActivity.presentation.model.EditableImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ImageEditActivityViewModel @Inject constructor(
    private val uploadImagesUseCase: UploadImagesUseCase
) : ViewModel() {

    // creating the pipe
    private val _oneTimeEvents = Channel<ImageEditActivityEvents>(Channel.BUFFERED)
    val oneTimeEvents = _oneTimeEvents.receiveAsFlow()

    // linking the pipe from viewModel to activity
    private fun sendOneTimeEvents(event: ImageEditActivityEvents) {
        viewModelScope.launch {
            _oneTimeEvents.send(event)
        }
    }

    private var nextImageId = 0L

    private val _uiState = MutableStateFlow(ImageEditActivityState())
    val uiState = _uiState.asStateFlow()


    // carrying One time events in the pipe
    fun onEvents(imageEditActivityEvents: ImageEditActivityEvents) {
        when (imageEditActivityEvents) {
            is ImageEditActivityEvents.OneTime.EditButton -> {

                _uiState.value = _uiState.value.copy(
                    currentEditImageId = imageEditActivityEvents.editableImage.id
                )
                sendOneTimeEvents(imageEditActivityEvents)
            }

            is ImageEditActivityEvents.OneTime.CloseActivity -> {
                sendOneTimeEvents(ImageEditActivityEvents.OneTime.CloseActivity)

            }

            is ImageEditActivityEvents.OneTime.DeleteButton -> {

                val deletedImageId = imageEditActivityEvents.imageToBeDeleted.id
                val updatedListDueToDeletion = _uiState.value.editableImages.filter {
                    (it.id != deletedImageId)
                    // filter collection function is used to keep objet that satisfy a given condition
                    //it returns a boolean.
                }
                _uiState.value = _uiState.value.copy(
                    editableImages = updatedListDueToDeletion
                )
            }

            is ImageEditActivityEvents.AddedImages -> {
                // received the added uriList and converted it to an EditableImage objet list
                // This part might be confusing [id = nextImageId++]
                // But the nextImageId give the size of the initial list
                //so if a listUri contains 3 uris, then the nextImage becomes 3 see below
                // nextImageId = uriListToEditableImageList.size.toLong()
                // Then the id of the first added image becomes what the variable of te nextImage holds then it get get incremented and the rest of the uri are subsequently assigned too
                val addedImageList = imageEditActivityEvents.uris.map { uri ->
                    EditableImage(
                        id = nextImageId++,
                        uri = uri
                    )
                }
                val updateList = _uiState.value.editableImages + addedImageList
                _uiState.value = _uiState.value.copy(
                    editableImages = updateList
                )
            }

            is ImageEditActivityEvents.OneTime.GalleryImageOneTimeEvent -> {
                sendOneTimeEvents(ImageEditActivityEvents.OneTime.GalleryImageOneTimeEvent)
            }

            is ImageEditActivityEvents.EditedImage -> {
                val updatedUriList = _uiState.value.editableImages.map {
                    if (imageEditActivityEvents.id == it.id) {
                        it.copy(
                            uri = imageEditActivityEvents.uri
                        )
                    } else {
                        it
                    }

                }
                _uiState.value = _uiState.value.copy(
                    editableImages = updatedUriList
                )
            }
            //As soon as the Image Edit ViewModel receives those URIs,
            // it gives every image a permanent ID. From that point onward,
            // the feature no longer works with plain URIs;
            // it works with EditableImage objects that contain both an ID and a URI.
            is ImageEditActivityEvents.ReceivedImagesFromCommentActivity -> {
                val uriListToEditableImageList =
                    imageEditActivityEvents.receivedUris.mapIndexed { index, uri ->
                        EditableImage(
                            id = index.toLong(),
                            uri = uri
                        )
                    }
                nextImageId = uriListToEditableImageList.size.toLong()

                _uiState.value = _uiState.value.copy(
                    editableImages = uriListToEditableImageList
                )
                Log.d(
                    "ImageEdit",
                    "ViewModel received = ${imageEditActivityEvents.receivedUris.size}"
                )

            }
            is ImageEditActivityEvents.OneTime.Next -> {
                viewModelScope.launch {
                    // i used map here because map is used for list transformation
                    // i wanted to transform from an editable object list to a Uri list
                    val extractedUri = _uiState.value.editableImages.map{
                        it.uri
                    }
                    val cloudinaryUrls = uploadImagesUseCase(extractedUri) // This is the place where request start,like the junction in viewModel was waiting for the result
                    _uiState.value = _uiState.value.copy(
                        uploadedUrls = cloudinaryUrls
                    )
                    sendOneTimeEvents(ImageEditActivityEvents.OneTime.Next)

                }


            }





        }
    }

}