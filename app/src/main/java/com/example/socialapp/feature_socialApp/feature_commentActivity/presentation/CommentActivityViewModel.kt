package com.example.socialapp.feature_socialApp.feature_commentActivity.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.feature_socialApp.feature_commentActivity.domain.use_case.CreatePostUseCase
import com.example.socialapp.feature_socialApp.feature_commentActivity.presentation.model.CreatePostRequest
import com.example.socialapp.feature_socialApp.feature_commentActivity.result_model.CreatePostResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentActivityViewModel @Inject constructor(val createPostUseCase: CreatePostUseCase) : ViewModel() {

    //1) private var _oneTimeEvents = Channel<CommentActivityEvents>(Channel.BUFFERED)
    //This creating the a mutable variable variable of type CommentActivityEvents this is the pipeline
    //The Channel.BUFFERED ensures that any action or event that is fired without the activity already created is queued and not lost
    //so when the activity is created the event is conveyed in the pipeline to the activity for implementation
    //2)val oneTimeEvents = _oneTimeEvents.receiveAsFlow()
    //this is the readable part of the logic. Also the pipeline is  modified into a flow that receptive by the activity
    //3)private fun sendOneTimeEvent(event : CommentActivityEvents){
    //    viewModelScope.launch {
    //        _oneTimeEvents.send(event)
    //    }
    //
    //}
    //this is a helper that put the event into the pipeline or flow ( this is my own understanding )
    //Putting the event in the pipe line requires a coroutine because that is how channel is built (inherent property)


    //For one time events
    private val _oneTimeEvents = Channel<CommentActivityEvents>(Channel.BUFFERED)  //writeable part
    val oneTimeEvents = _oneTimeEvents.receiveAsFlow()  // readable part

    // For regular events (state, stuff that persist on the screen
    private val _state = MutableStateFlow(CommentActivityState())
    val state = _state.asStateFlow()


    // to place content in the channel pipe, a coroutine is needed
    // This is coroutine i used viewModelScope.launch{}
    private fun sendOneTimeEvent(event : CommentActivityEvents){
        Log.d("Flow", "3. Sending event $event")
        viewModelScope.launch {
            _oneTimeEvents.send(event)
        }

    }



    fun onEvents (commentActivityEvents : CommentActivityEvents) {
        when (commentActivityEvents){
            is CommentActivityEvents.OneTime.CloseCommentActivity -> {
               sendOneTimeEvent(CommentActivityEvents.OneTime.CloseCommentActivity)
            }
            is CommentActivityEvents.OneTime.CloseImageViewButtonIcon -> {
               sendOneTimeEvent(CommentActivityEvents.OneTime.CloseImageViewButtonIcon)
            }
            is CommentActivityEvents.OnImageSelected -> {
              _state.value = _state.value.copy(
                  selectedUri = commentActivityEvents.uris
              )
                sendOneTimeEvent(CommentActivityEvents.OneTime.NavigatingToEditingScreen(commentActivityEvents.uris))
                Log.d("Flow", "2. OnImageSelected")

            }
            is CommentActivityEvents.OneTime.ReselectGalleryImagePicker -> {
                sendOneTimeEvent(CommentActivityEvents.OneTime.ReselectGalleryImagePicker)
            }
            is CommentActivityEvents.ShareIdea -> {
                _state.value = _state.value.copy(
                    ideaMessage = commentActivityEvents.message
                )

            }
            is CommentActivityEvents.OnUploadedUrls -> {
                _state.value = _state.value.copy(
                    uploadedUrls = commentActivityEvents.uploadedUrls
                )
            }
            is CommentActivityEvents.OneTime.PostUserContent -> {
                viewModelScope.launch {
                    val content = _state.value.ideaMessage
                    val uploadedUrl = _state.value.uploadedUrls
                    val post = CreatePostRequest(content,uploadedUrl)
                    val result = createPostUseCase(post)
                    when (result) {
                        is CreatePostResult.Success -> {
                            sendOneTimeEvent(CommentActivityEvents.OneTime.PostUserContentSuccess(result.message))
                        }

                        is CreatePostResult.Error -> {
                            sendOneTimeEvent(CommentActivityEvents.OneTime.PostUserContentError(result.error))
                        }
                    }



                }

            }
            is CommentActivityEvents.OneTime.GalleryImagePicker -> {
                sendOneTimeEvent(CommentActivityEvents.OneTime.GalleryImagePicker)
            }

            else -> {

            }
        }

    }
}