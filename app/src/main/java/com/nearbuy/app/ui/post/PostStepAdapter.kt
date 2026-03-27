package com.nearbuy.app.ui.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.nearbuy.app.data.mock.MockData
import com.nearbuy.app.databinding.*

class PostStepAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> ImageViewHolder(ItemPostStepImageBinding.inflate(inflater, parent, false))
            1 -> DetailsViewHolder(ItemPostStepDetailsBinding.inflate(inflater, parent, false))
            2 -> CategoryViewHolder(ItemPostStepCategoryBinding.inflate(inflater, parent, false))
            3 -> PriceViewHolder(ItemPostStepPriceBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryViewHolder -> holder.bind()
            is PriceViewHolder -> holder.bind()
        }
    }

    override fun getItemCount(): Int = 4

    class ImageViewHolder(binding: ItemPostStepImageBinding) : RecyclerView.ViewHolder(binding.root)
    
    class DetailsViewHolder(val binding: ItemPostStepDetailsBinding) : RecyclerView.ViewHolder(binding.root)
    
    class CategoryViewHolder(val binding: ItemPostStepCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            if (binding.cgPostCategories.childCount == 0) {
                MockData.categories.forEach { category ->
                    val chip = Chip(binding.root.context).apply {
                        text = category.name
                        isCheckable = true
                    }
                    binding.cgPostCategories.addView(chip)
                }
            }
        }
    }
    
    class PriceViewHolder(val binding: ItemPostStepPriceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.priceSlider.addOnChangeListener { _, value, _ ->
                binding.tvPriceLabel.text = "Price: Rs. ${value.toInt()}"
            }
        }
    }
}
