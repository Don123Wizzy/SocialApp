package com.example.socialapp.feature_socialApp.feature_homeFragment.presentation

import com.example.socialapp.data.Post

sealed class HomeFragmentEvents {

    object FloatingActionButton : HomeFragmentEvents()
    data class LikeButton(val post : Post) : HomeFragmentEvents()
    data class ProfileButton(val userId : String) : HomeFragmentEvents()
    data class FollowButton(val userId : String) : HomeFragmentEvents()
    data class ReadMoreReadLess(val userId : String) : HomeFragmentEvents()
    data class ShareButton(val userId : String) : HomeFragmentEvents()
    data class CommentButton(val userId : String) : HomeFragmentEvents()
    data class OnMoreButton(val userId : String) : HomeFragmentEvents()
    data class DeleteButton(val post : Post): HomeFragmentEvents()
    data class EditButton(val post : Post) : HomeFragmentEvents()
    data class GetPosts(val post : Post) : HomeFragmentEvents()


}