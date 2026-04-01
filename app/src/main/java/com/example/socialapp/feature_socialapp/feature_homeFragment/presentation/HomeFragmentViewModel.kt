package com.example.socialapp.feature_socialapp.feature_homeFragment.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeFragmentViewModel : ViewModel() {

    val _event = MutableLiveData< EventClass<HomeFragmentOneTimeEvent>>()
    val event : LiveData<EventClass<HomeFragmentOneTimeEvent>> get() = _event

    fun onEvents (homeFragmentOneTimeEvent : HomeFragmentOneTimeEvent){
        when(homeFragmentOneTimeEvent){
            is HomeFragmentOneTimeEvent.FloatingActionButton -> {
                _event.value = EventClass(HomeFragmentOneTimeEvent.FloatingActionButton)

            }
        }

    }

}