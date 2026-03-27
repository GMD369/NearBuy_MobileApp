package com.nearbuy.app.ui.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.nearbuy.app.R
import com.nearbuy.app.data.mock.MockData
import com.nearbuy.app.databinding.LayoutFilterBottomSheetBinding

class FilterBottomSheet(
    private val onApply: (min: Double, max: Double, category: String?, condition: String?, swapOnly: Boolean) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: LayoutFilterBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutFilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategories()
        setupButtons()
    }

    private fun setupCategories() {
        binding.cgCategories.removeAllViews()
        val context = requireContext()
        MockData.categories.forEach { category ->
            val chip = Chip(ContextThemeWrapper(context, com.google.android.material.R.style.Widget_Material3_Chip_Filter)).apply {
                text = category.name
                isCheckable = true
            }
            binding.cgCategories.addView(chip)
        }
    }

    private fun setupButtons() {
        binding.btnApplyFilter.setOnClickListener {
            val values = binding.priceRangeSlider.values
            
            val selectedCategoryId = binding.cgCategories.checkedChipId
            val category = if (selectedCategoryId != View.NO_ID) {
                binding.cgCategories.findViewById<Chip>(selectedCategoryId).text.toString()
            } else null
            
            val condition = when (binding.cgCondition.checkedChipId) {
                R.id.chipConditionNew -> "New"
                R.id.chipConditionUsed -> "Used"
                else -> null
            }

            val swapOnly = binding.switchSwap.isChecked

            onApply(values[0].toDouble(), values[1].toDouble(), category, condition, swapOnly)
            dismiss()
        }

        binding.btnResetFilter.setOnClickListener {
            resetFilters()
        }
    }

    private fun resetFilters() {
        binding.priceRangeSlider.setValues(0f, 1000000f)
        binding.cgCategories.clearCheck()
        binding.cgCondition.clearCheck()
        binding.switchSwap.isChecked = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "FilterBottomSheet"
        fun newInstance(onApply: (min: Double, max: Double, category: String?, condition: String?, swapOnly: Boolean) -> Unit) = 
            FilterBottomSheet(onApply)
    }
}
