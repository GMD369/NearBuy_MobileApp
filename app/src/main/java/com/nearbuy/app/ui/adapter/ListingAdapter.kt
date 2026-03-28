package com.nearbuy.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.nearbuy.app.R
import com.nearbuy.app.data.model.Listing
import com.nearbuy.app.databinding.ItemListingBinding
import java.io.File
import java.text.NumberFormat
import java.util.Locale

class ListingAdapter(
    private val onItemClick: (Listing) -> Unit,
    private val onFavoriteClick: ((Listing) -> Unit)? = null
) : ListAdapter<Listing, ListingAdapter.ListingViewHolder>(DIFF_CALLBACK) {

    private var favoriteIds: Set<String> = emptySet()

    fun updateFavorites(ids: Set<String>) {
        favoriteIds = ids
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val binding = ItemListingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ListingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ListingViewHolder(private val binding: ItemListingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_ID.toInt()) onItemClick(getItem(pos))
            }
        }

        fun bind(listing: Listing) {
            val formatted = NumberFormat.getNumberInstance(Locale("en", "PK"))
                .format(listing.price.toLong())
            binding.tvListingPrice.text    = "Rs. $formatted"
            binding.tvListingTitle.text    = listing.title
            binding.tvListingLocation.text = listing.location
            binding.chipSwapBadge.isVisible = listing.isSwapAllowed
            binding.chipDealBadge.isVisible = listing.price < 5000
            binding.tvSellerRatingOnCard.text = binding.root.context.getString(R.string.label_rating)

            // Save icon
            val isFav = listing.id in favoriteIds
            binding.btnFavorite.setImageResource(
                if (isFav) R.drawable.ic_bookmark_on
                else R.drawable.ic_bookmark_off
            )
            binding.btnFavorite.setOnClickListener {
                onFavoriteClick?.invoke(listing)
            }

            val imagePath = listing.imagePaths.firstOrNull()
            if (!imagePath.isNullOrBlank()) {
                binding.ivListingImage.load(File(imagePath)) {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_background)
                    error(R.drawable.ic_launcher_background)
                }
            } else {
                binding.ivListingImage.load(R.drawable.ic_launcher_background)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Listing>() {
            override fun areItemsTheSame(a: Listing, b: Listing) = a.id == b.id
            override fun areContentsTheSame(a: Listing, b: Listing) = a == b
        }
    }
}
