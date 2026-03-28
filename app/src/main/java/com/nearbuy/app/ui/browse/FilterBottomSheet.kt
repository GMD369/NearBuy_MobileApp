package com.nearbuy.app.ui.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.res.ColorStateList
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

        binding.btnRemoveFilters.setOnClickListener {
            binding.etMinPrice.text?.clear()
            binding.etMaxPrice.text?.clear()
            binding.chipGroupSort.check(binding.chipNewest.id)
            binding.chipGroupCategory.clearCheck()
            binding.chipGroupCondition.clearCheck()
            onApply(Double.MIN_VALUE, Double.MAX_VALUE, null, null, false)
            dismiss()
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
        val orange = requireContext().getColor(android.R.color.holo_orange_dark)
        val orangeLight = 0xFFFFF0E6.toInt()
        val gray = 0xFFF4F4F4.toInt()
        val white = requireContext().getColor(android.R.color.white)

        binding.chipGroupCategory.removeAllViews()
        MockData.categories.forEach { category ->
            val chip = Chip(ContextThemeWrapper(requireContext(), com.google.android.material.R.style.Widget_Material3_Chip_Filter)).apply {
                text = category.name
                isCheckable = true
                chipBackgroundColor = ColorStateList.valueOf(gray)
                setTextColor(0xFF666666.toInt())
                setOnCheckedChangeListener { btn, checked ->
                    btn as Chip
                    btn.chipBackgroundColor = ColorStateList.valueOf(if (checked) orangeLight else gray)
                    btn.setTextColor(if (checked) orange else 0xFF666666.toInt())
                }
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
