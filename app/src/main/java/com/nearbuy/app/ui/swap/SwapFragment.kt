package com.nearbuy.app.ui.swap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nearbuy.app.R
import com.nearbuy.app.databinding.FragmentSwapBinding
import com.nearbuy.app.ui.auth.AuthViewModel

class SwapFragment : Fragment() {

    private var _binding: FragmentSwapBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSwapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!authViewModel.isLoggedIn()) {
            binding.layoutGuestSwap.root.visibility = View.VISIBLE
            binding.swapTabs.visibility = View.GONE
            binding.rvSwapProposals.visibility = View.GONE
            binding.layoutGuestSwap.btnGuestSwapLogin.setOnClickListener {
                findNavController().navigate(R.id.nav_login)
            }
            binding.layoutGuestSwap.btnGuestSwapHome.setOnClickListener {
                findNavController().navigate(R.id.nav_home)
            }
            return
        }

        // Swap proposals will be loaded here once backend is available
        binding.rvSwapProposals.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
