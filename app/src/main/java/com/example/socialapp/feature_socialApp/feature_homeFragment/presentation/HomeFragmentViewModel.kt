package com.example.socialapp.feature_socialApp.feature_homeFragment.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.feature_socialApp.feature_homeFragment.domain.use_case.DeletePostUseCase
import com.example.socialapp.feature_socialApp.feature_homeFragment.domain.use_case.EditPostUseCase
import com.example.socialapp.feature_socialApp.feature_homeFragment.domain.use_case.GetPostsUseCase
import com.example.socialapp.feature_socialApp.feature_homeFragment.domain.use_case.LikePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val likePostUseCase: LikePostUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val editPostUseCase: EditPostUseCase,
    private val getPostsUseCase: GetPostsUseCase
) : ViewModel() {


    val _event = Channel<HomeFragmentEvents>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    fun sendOneTimeEvents(event: HomeFragmentEvents) {
        viewModelScope.launch {
            _event.send(event)
        }
    }

    private val _uiState = MutableStateFlow(HomeFragmentState())
    val uiState = _uiState.asStateFlow()

    init {
        getPosts()
    }

    fun onEvents(homeFragmentEvents: HomeFragmentEvents) {
        when (homeFragmentEvents) {
            is HomeFragmentEvents.FloatingActionButton -> {
                sendOneTimeEvents(homeFragmentEvents)
            }

            is HomeFragmentEvents.LikeButton -> {
                viewModelScope.launch {
                    likePostUseCase(homeFragmentEvents.post)
                }
            }

            is HomeFragmentEvents.CommentButton -> {

            }

            is HomeFragmentEvents.ProfileButton -> {

            }

            is HomeFragmentEvents.FollowButton -> {

            }

            is HomeFragmentEvents.ShareButton -> {

            }

            is HomeFragmentEvents.ReadMoreReadLess -> {

            }

            is HomeFragmentEvents.DeleteButton -> {
                viewModelScope.launch {
                    deletePostUseCase(homeFragmentEvents.post)
                }
            }

            is HomeFragmentEvents.EditButton -> {
                viewModelScope.launch {
                    editPostUseCase(homeFragmentEvents.post)
                }

            }
            else -> {}
        }

    }

    private fun getPosts(){
        viewModelScope.launch {
            // we used the collect here because when the function (getPostsUseCase()) is called,
            // it returns a flow type hence, it an call any function (e.g collect) in the Flow interface
            // Since it returns a Flow, we can collect it to receive
            // every new list of posts emitted by Firestore.
            val postsFlow = getPostsUseCase()
            postsFlow.collect { updatedList ->
                Log.d("HOME_VIEWMODEL", "Received posts: ${updatedList.size}")
                _uiState.value = _uiState.value.copy(
                    posts = updatedList
                )
            }
        }
    }

}