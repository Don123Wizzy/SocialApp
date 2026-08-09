package com.example.socialapp.feature_socialApp.feature_profileFragment.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.example.socialapp.R
import com.example.socialapp.ReportActivity
import com.example.socialapp.UserProfile
import com.example.socialapp.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProfileFragmentScreen : Fragment() {
    private var _binding : FragmentProfileBinding? = null
    val binding get() = _binding!!
    private val viewModel : ProfileFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // android reads my xml files and creates all the views
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        // android attach the fragment views to it root layout i.e viewGroup
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnProfile.setOnClickListener {
            viewModel.onEvents(ProfileFragmentEvents.OpenProfileActivity)

        }
        binding.btnLogout.setOnClickListener {
            viewModel.onEvents(ProfileFragmentEvents.Logout)

        }
        binding.btnSettings.setOnClickListener {
            viewModel.onEvents(ProfileFragmentEvents.OpenSettingsActivity)

        }
        binding.btnReportAnIssue.setOnClickListener {
            viewModel.onEvents(ProfileFragmentEvents.OpenReportActivity)

        }
        binding.btnFriendList.setOnClickListener {
            viewModel.onEvents(ProfileFragmentEvents.OpenFriendListActivity)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.events.collect { oneTimeEvents ->
                        when (oneTimeEvents){
                            is ProfileFragmentEvents.ErrorMessage ->{
                                Toast.makeText(requireContext(),oneTimeEvents.message, Toast.LENGTH_SHORT).show()
                            }
                            is ProfileFragmentEvents.OpenProfileActivity -> {
                                val intent = Intent(requireActivity(), UserProfile::class.java)
                                startActivity(intent)
                            }
                            is ProfileFragmentEvents.OpenSettingsActivity -> {}
                            is ProfileFragmentEvents.OpenReportActivity -> {
                                val intent = Intent(requireActivity(), ReportActivity::class.java)
                                startActivity(intent)
                            }
                            is ProfileFragmentEvents.OpenFriendListActivity -> {}
                            is ProfileFragmentEvents.OpenNotificationActivity -> {}
                            is ProfileFragmentEvents.Logout -> {}
                        }

                    }
                }
                launch {
                    viewModel.uiState.collect { profileFragmentState->
                        Glide.with(requireContext())
                            .load(profileFragmentState.userProfilePicture)
                            .placeholder(R.drawable.ic_placeholder)
                            .into(binding.imProfileImage)

                        binding.tvProfileUserName.text = profileFragmentState.userName

                    }
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}