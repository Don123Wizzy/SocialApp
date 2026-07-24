package com.example.socialapp.feature_socialApp.feature_homeFragment.presentation

import com.example.socialapp.data.Post

data class HomeFragmentState(
    val posts : List<Post> = emptyList()
)
