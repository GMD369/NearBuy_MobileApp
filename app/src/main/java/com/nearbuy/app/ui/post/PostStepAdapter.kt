package com.nearbuy.app.ui.post

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.nearbuy.app.data.model.Listing
import com.nearbuy.app.data.repository.ListingRepository
import com.nearbuy.app.databinding.*

class PostStepAdapter(
    private val onPickImages: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Collected form state
    val selectedImagePaths: MutableList<String> = mutableListOf()
    var title: String = ""
    var description: String = ""
    var selectedCategory: String = ""
    var price: Double = 1000.0
    var isSwapAllowed: Boolean = false

    private var imageViewHolder: ImageViewHolder? = null

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
            is ImageViewHolder    -> { imageViewHolder = holder; holder.bind() }
            is DetailsViewHolder  -> holder.bind()
            is CategoryViewHolder -> holder.bind()
            is PriceViewHolder    -> holder.bind()
        }
    }

    override fun getItemCount(): Int = 4

    fun refreshImageStep() {
        imageViewHolder?.refreshImages()
    }

    fun buildListing(sellerId: String, sellerName: String, userLocation: String): Listing? {
        if (title.isBlank()) return null
        return Listing(
            sellerId      = sellerId,
            sellerName    = sellerName,
            title         = title,
            description   = description,
            price         = price,
            category      = selectedCategory.ifBlank { "Other" },
            condition     = "Used",
            location      = userLocation,
            imagePaths    = selectedImagePaths.toList(),
            isSwapAllowed = isSwapAllowed
        )
    }

    inner class ImageViewHolder(val binding: ItemPostStepImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var imageAdapter: SelectedImageAdapter

        fun bind() {
            imageAdapter = SelectedImageAdapter(selectedImagePaths) { position ->
                selectedImagePaths.removeAt(position)
                imageAdapter.notifyItemRemoved(position)
                imageAdapter.notifyItemRangeChanged(position, selectedImagePaths.size)
                refreshImages()
            }
            binding.rvSelectedImages.apply {
                layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = imageAdapter
            }
            refreshImages()
            binding.btnUploadImage.setOnClickListener {
                if (selectedImagePaths.size < 5) onPickImages()
            }
        }

        fun refreshImages() {
            val hasImages = selectedImagePaths.isNotEmpty()
            binding.rvSelectedImages.visibility = if (hasImages) View.VISIBLE else View.GONE
            binding.tvImageCount.text = binding.root.context.getString(com.nearbuy.app.R.string.label_image_count, selectedImagePaths.size)
            imageAdapter.notifyDataSetChanged()
        }
    }

    inner class DetailsViewHolder(val binding: ItemPostStepDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.etTitle.setText(title)
            binding.etDescription.setText(description)
            binding.etTitle.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { title = s.toString() }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            binding.etDescription.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { description = s.toString() }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    inner class CategoryViewHolder(val binding: ItemPostStepCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            if (binding.cgPostCategories.childCount == 0) {
                ListingRepository.CATEGORIES.drop(1).forEach { cat ->
                    val chip = Chip(binding.root.context).apply {
                        text = cat
                        isCheckable = true
                        isChecked = (cat == selectedCategory)
                        setOnCheckedChangeListener { _, checked ->
                            if (checked) selectedCategory = cat
                        }
                    }
                    binding.cgPostCategories.addView(chip)
                }
            }
        }
    }

    inner class PriceViewHolder(val binding: ItemPostStepPriceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.priceSlider.value = price.toFloat().coerceIn(0f, 500000f)
            binding.tvPriceLabel.text = binding.root.context.getString(com.nearbuy.app.R.string.label_price_rs, price.toInt())
            binding.switchSwap.isChecked = isSwapAllowed
            binding.priceSlider.addOnChangeListener { _, value, _ ->
                price = value.toDouble()
                binding.tvPriceLabel.text = "Price: Rs. ${value.toInt()}"
            }
            binding.switchSwap.setOnCheckedChangeListener { _, checked ->
                isSwapAllowed = checked
            }
        }
    }
}
