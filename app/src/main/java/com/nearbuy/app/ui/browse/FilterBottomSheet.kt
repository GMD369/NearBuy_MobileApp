package com.nearbuy.app.ui.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.nearbuy.app.data.mock.MockData
import com.nearbuy.app.databinding.BottomSheetFilterBinding

class FilterBottomSheet(
    private val onApply: (min: Double, max: Double, category: String?, condition: String?, swapOnly: Boolean) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategories()

        binding.tvResetFilters.setOnClickListener {
            binding.etMinPrice.text?.clear()
            binding.etMaxPrice.text?.clear()
            binding.chipGroupSort.check(binding.chipNewest.id)
            binding.chipGroupCategory.clearCheck()
            binding.chipGroupCondition.clearCheck()
        }

        binding.btnApplyFilters.setOnClickListener {
            val min = binding.etMinPrice.text?.toString()?.toDoubleOrNull() ?: Double.MIN_VALUE
            val max = binding.etMaxPrice.text?.toString()?.toDoubleOrNull() ?: Double.MAX_VALUE

            val selectedCategoryId = binding.chipGroupCategory.checkedChipId
            val category = if (selectedCategoryId != View.NO_ID)
                binding.chipGroupCategory.findViewById<Chip>(selectedCategoryId).text.toString()
            else null

            val condition = when (binding.chipGroupCondition.checkedChipId) {
                binding.chipNew.id         -> "New"
                binding.chipUsed.id        -> "Used"
                binding.chipRefurbished.id -> "Refurbished"
                else                       -> null
            }

            onApply(min, max, category, condition, false)
            dismiss()
        }
    }

    private fun setupCategories() {
        binding.chipGroupCategory.removeAllViews()
        MockData.categories.forEach { category ->
            val chip = Chip(ContextThemeWrapper(requireContext(), com.google.android.material.R.style.Widget_Material3_Chip_Filter)).apply {
                text = category.name
                isCheckable = true
            }
            binding.chipGroupCategory.addView(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "FilterBottomSheet"
    }
}
