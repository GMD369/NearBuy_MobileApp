package com.nearbuy.app.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nearbuy.app.NearBuyApplication
import com.nearbuy.app.R
import com.nearbuy.app.databinding.FragmentEditProfileBinding
import java.io.File

class EditProfileBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    
    // Scoped to the parent fragment (ProfileFragment) to share the same instance
    private val profileViewModel: ProfileViewModel by viewModels(ownerProducer = { requireParentFragment() })

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { saveProfileImage(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe user data to pre-fill fields and handle updates
        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                if (binding.etEditName.text.isNullOrBlank()) {
                    binding.etEditName.setText(it.name)
                    binding.etEditPhone.setText(it.phone)
                    binding.etEditLocation.setText(it.location)
                    binding.etEditBio.setText(it.bio)
                }
                
                if (it.profileImagePath.isNotBlank()) {
                    binding.ivEditProfileImage.load(File(it.profileImagePath)) {
                        crossfade(true)
                        placeholder(R.drawable.ic_launcher_background)
                        error(R.drawable.ic_launcher_background)
                    }
                } else {
                    binding.ivEditProfileImage.load(R.drawable.ic_launcher_background)
                }
            }
        }

        binding.btnChangePhoto.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnSaveProfile.setOnClickListener {
            val name = binding.etEditName.text.toString().trim()
            if (name.isBlank()) {
                binding.tilEditName.error = "Name cannot be empty"
                return@setOnClickListener
            }
            binding.tilEditName.error = null
            
            val phone    = binding.etEditPhone.text.toString().trim()
            val location = binding.etEditLocation.text.toString().trim()
            val bio      = binding.etEditBio.text.toString().trim()

            profileViewModel.updateProfile(
                name     = name,
                phone    = phone,
                location = location,
                bio      = bio
            )
            
            Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    private fun saveProfileImage(uri: Uri) {
        try {
            val app       = requireActivity().application as NearBuyApplication
            val targetDir = app.localStorageManager.getProfileImagesDir()
            val fileName  = "profile_${System.currentTimeMillis()}.jpg"
            val target    = File(targetDir, fileName)

            requireContext().contentResolver.openInputStream(uri)?.use { input ->
                target.outputStream().use { output -> input.copyTo(output) }
            }

            profileViewModel.updateProfileImage(target.absolutePath)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
