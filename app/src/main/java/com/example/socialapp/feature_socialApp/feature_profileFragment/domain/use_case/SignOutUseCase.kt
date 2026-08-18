package com.example.socialapp.feature_socialApp.feature_profileFragment.domain.use_case

import com.example.socialapp.feature_socialApp.feature_profileFragment.domain.repository.UserDetailRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val repository: UserDetailRepository
) {
    operator fun invoke () {
        repository.logOut()
    }
}