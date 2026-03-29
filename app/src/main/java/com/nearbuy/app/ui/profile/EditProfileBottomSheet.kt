package com.nearbuy.app.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
    
    private val profileViewModel: ProfileViewModel by viewModels(ownerProducer = { requireParentFragment() })

    private val provinces = listOf("Punjab", "Sindh", "Khyber Pakhtunkhwa", "Balochistan", "Islamabad Capital Territory")
    private val citiesMap = mapOf(
        "Punjab" to listOf("Lahore", "Faisalabad", "Rawalpindi", "Gujranwala", "Multan", "Sialkot", "Bahawalpur", "Sargodha"),
        "Sindh" to listOf("Karachi", "Hyderabad", "Sukkur", "Larkana", "Nawabshah", "Mirpur Khas"),
        "Khyber Pakhtunkhwa" to listOf("Peshawar", "Mardan", "Abbottabad", "Mingora", "Kohat", "Dera Ismail Khan"),
        "Balochistan" to listOf("Quetta", "Turbat", "Khuzdar", "Hub", "Chaman", "Gwadar"),
        "Islamabad Capital Territory" to listOf("Islamabad")
    )

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

        setupLocationSpinners()

        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                if (binding.etEditName.text.isNullOrBlank()) {
                    binding.etEditName.setText(it.name)
                    binding.etEditPhone.setText(it.phone)
                    // Location logic handled in setupLocationSpinners pre-fill
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
            
            val city = binding.acEditLocation.text.toString().trim()
            if (city.isBlank()) {
                Toast.makeText(requireContext(), "Please select a city", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            profileViewModel.updateProfile(
                name     = name,
                phone    = binding.etEditPhone.text.toString().trim(),
                location = city,
                bio      = binding.etEditBio.text.toString().trim()
            )
            
            Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    private fun setupLocationSpinners() {
        val provinceAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, provinces)
        binding.acEditProvince.setAdapter(provinceAdapter)

        binding.acEditProvince.setOnItemClickListener { _, _, position, _ ->
            val selectedProvince = provinces[position]
            updateCityDropdown(selectedProvince)
        }

        // Pre-fill location if user already has one
        profileViewModel.user.value?.location?.let { currentLocation ->
            if (currentLocation.isNotBlank()) {
                // Find which province this city belongs to
                val province = citiesMap.entries.find { it.value.contains(currentLocation) }?.key
                if (province != null) {
                    binding.acEditProvince.setText(province, false)
                    updateCityDropdown(province)
                    binding.acEditLocation.setText(currentLocation, false)
                }
            }
        }
    }

    private fun updateCityDropdown(province: String) {
        val cities = citiesMap[province] ?: emptyList()
        val cityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, cities)
        binding.acEditLocation.setAdapter(cityAdapter)
        binding.acEditLocation.setText("") // Clear previous selection
        binding.tilEditLocation.isEnabled = true
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
