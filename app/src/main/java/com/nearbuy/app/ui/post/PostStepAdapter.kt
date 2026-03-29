package com.nearbuy.app.ui.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.nearbuy.app.data.mock.MockData
import com.nearbuy.app.data.model.Listing
import com.nearbuy.app.databinding.*
import java.util.*

class PostStepAdapter(private val onPickImages: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var selectedImagePaths = mutableListOf<String>()
    var title: String = ""
    var description: String = ""
    var category: String? = null
    var price: Double = 0.0
    var isSwapAllowed: Boolean = false

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
            is ImageViewHolder -> holder.bind()
            is DetailsViewHolder -> holder.bind()
            is CategoryViewHolder -> holder.bind()
            is PriceViewHolder -> holder.bind()
        }
    }

    override fun getItemCount(): Int = 4

    fun refreshImageStep() {
        notifyItemChanged(0)
    }

    fun buildListing(sellerId: String, sellerName: String, sellerPhone: String, userLocation: String): Listing? {
        if (title.isBlank()) return null
        return Listing(
            id = UUID.randomUUID().toString(),
            sellerId = sellerId,
            sellerName = sellerName,
            sellerPhone = sellerPhone,
            title = title,
            description = description,
            price = price,
            category = category ?: "Other",
            condition = "New", // Default for now
            location = userLocation,
            imagePaths = selectedImagePaths.toList(),
            isSwapAllowed = isSwapAllowed
        )
    }

    inner class ImageViewHolder(val binding: ItemPostStepImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.btnUploadImage.setOnClickListener { onPickImages() }
            binding.tvImageCount.text = "${selectedImagePaths.size} / 5 photos selected"
        }
    }
    
    inner class DetailsViewHolder(val binding: ItemPostStepDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.etTitle.setText(title)
            binding.etDescription.setText(description)
            
            binding.etTitle.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    title = s.toString()
                }
                override fun afterTextChanged(s: android.text.Editable?) {}
            })
            
            binding.etDescription.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    description = s.toString()
                }
                override fun afterTextChanged(s: android.text.Editable?) {}
            })
        }
    }
    
    inner class CategoryViewHolder(val binding: ItemPostStepCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            if (binding.cgPostCategories.childCount == 0) {
                MockData.categories.forEach { cat ->
                    val chip = Chip(ContextThemeWrapper(binding.root.context, com.google.android.material.R.style.Widget_Material3_Chip_Filter)).apply {
                        text = cat.name
                        isCheckable = true
                        isChecked = (category == cat.name)
                        setOnClickListener { category = cat.name }
                    }
                    binding.cgPostCategories.addView(chip)
                }
            }
        }
    }
    
    inner class PriceViewHolder(val binding: ItemPostStepPriceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.priceSlider.value = price.toFloat().coerceIn(0f, 1000000f)
            binding.tvPriceLabel.text = "Price: Rs. ${price.toInt()}"
            binding.switchSwap.isChecked = isSwapAllowed

            binding.priceSlider.addOnChangeListener { _, value, _ ->
                price = value.toDouble()
                binding.tvPriceLabel.text = "Price: Rs. ${value.toInt()}"
            }

            binding.switchSwap.setOnCheckedChangeListener { _, isChecked ->
                isSwapAllowed = isChecked
            }
        }
    }
}
