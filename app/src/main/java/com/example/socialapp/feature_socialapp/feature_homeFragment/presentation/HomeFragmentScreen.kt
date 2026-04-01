package com.example.socialapp.feature_socialapp.feature_homeFragment.presentation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.socialapp.CommentActivity
import com.example.socialapp.databinding.FragmentHomeBinding


class HomeFragmentScreen : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel : HomeFragmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       _binding = FragmentHomeBinding.inflate(inflater,container,false)

        binding.btnFAB.setOnClickListener {
            viewModel.onEvents(HomeFragmentOneTimeEvent.FloatingActionButton)
        }

        viewModel.event.observe(viewLifecycleOwner) { event ->
            event.ifContentHasBeenHandled()?.let { homeFragmentOneTimeEvent ->
                when(homeFragmentOneTimeEvent ){
                    is HomeFragmentOneTimeEvent.FloatingActionButton -> {
                        val intent = Intent(requireActivity(), CommentActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()

                    }
                }

            }

        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}