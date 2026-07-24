package com.example.socialapp.feature_socialApp.feature_homeFragment.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.adapters.HomeFragmentAdapter
import com.example.socialapp.databinding.FragmentHomeBinding
import com.example.socialapp.feature_socialApp.feature_commentActivity.presentation.CommentActivityScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragmentScreen : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel : HomeFragmentViewModel by viewModels()
    private lateinit var homeFragmentAdapter: HomeFragmentAdapter
    private var isBottomNavVisible = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        homeFragmentAdapter = HomeFragmentAdapter(
            onLikeClick = {
                viewModel.onEvents(HomeFragmentEvents.LikeButton(it))
            },
            onCommentClick = {
                viewModel.onEvents(HomeFragmentEvents.CommentButton(it.userId))
            },
            onProfileClick = {
                viewModel.onEvents(HomeFragmentEvents.ProfileButton(it.userId))
            },
            onMoreClick = {
                viewModel.onEvents(HomeFragmentEvents.OnMoreButton(it.userId))
            },
            onShareClick = {
                viewModel.onEvents(HomeFragmentEvents.ShareButton(it.userId))
            },
            onReadMoreClick = {
                viewModel.onEvents(HomeFragmentEvents.ReadMoreReadLess(it.userId))
            }
        )

        binding.rvRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvRecyclerView.adapter = homeFragmentAdapter

        setupBottomNavScrollListener()

        binding.btnFAB.setOnClickListener {
            viewModel.onEvents(HomeFragmentEvents.FloatingActionButton)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.event.collect { homeFragmentEvents ->
                        when (homeFragmentEvents){
                            is HomeFragmentEvents.FloatingActionButton -> {
                                val intent = Intent(requireActivity(), CommentActivityScreen::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                            else -> {}

                        }

                    }
                }
                launch {
                    viewModel.uiState.collect { homeFragmentState ->
                        Log.d("HOME_STATE", "Posts size: ${homeFragmentState.posts.size}")
                        homeFragmentAdapter.submitList(homeFragmentState.posts)
                    }
                }

            }
        }
    }


    interface BottomNavController {
        fun hideBottomNav()
        fun showBottomNav()
    }

    private fun setupBottomNavScrollListener() {
        binding.rvRecyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {

                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    super.onScrolled(recyclerView, dx, dy)

                    val controller = activity as? BottomNavController ?: return

                    if (dy > 0 && isBottomNavVisible) {
                        controller.hideBottomNav()
                        isBottomNavVisible = false
                    }

                    if (dy < 0 && !isBottomNavVisible) {
                        controller.showBottomNav()
                        isBottomNavVisible = true
                    }
                }
            }
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}