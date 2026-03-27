package com.nearbuy.app.ui.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import coil.size.Precision
import com.nearbuy.app.R
import com.nearbuy.app.data.model.Listing
import com.nearbuy.app.databinding.ItemListingBinding
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class ListingAdapter(private val onItemClick: (Listing, View) -> Unit) :
    ListAdapter<Listing, ListingAdapter.ListingViewHolder>(ListingDiffCallback()) {

    private var lastPosition = -1

    private val priceFormatter = NumberFormat.getCurrencyInstance(Locale("en", "PK")).apply {
        maximumFractionDigits = 0
    }

    private val colorGreatDeal = ColorStateList.valueOf(Color.parseColor("#4CAF50"))
    private val colorHighPrice = ColorStateList.valueOf(Color.parseColor("#F44336"))
    private val colorFairPrice = ColorStateList.valueOf(Color.parseColor("#FF9800"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val binding = ItemListingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        holder.bind(getItem(position))
        setAnimation(holder.itemView, position)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.slide_up_fade)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun onViewDetachedFromWindow(holder: ListingViewHolder) {
        holder.itemView.clearAnimation()
        super.onViewDetachedFromWindow(holder)
    }

    inner class ListingViewHolder(private val binding: ItemListingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listing: Listing) {
            binding.apply {
                tvListingTitle.text = listing.title
                tvListingPrice.text = formatPrice(listing.price)
                tvListingLocation.text = listing.location
                tvSellerRatingOnCard.text = listing.user.rating.toString()
                
                chipSwapBadge.visibility = if (listing.isSwapAvailable) View.VISIBLE else View.GONE
                
                setupDealBadge(listing.price, listing.category)
                
                btnFavorite.setOnClickListener {
                    val isFav = it.tag == "fav"
                    // Micro-interaction: Heart pop animation
                    val animation = AnimationUtils.loadAnimation(it.context, R.anim.heart_pop)
                    it.startAnimation(animation)
                    
                    if (isFav) {
                        btnFavorite.setImageResource(android.R.drawable.btn_star_big_off)
                        it.tag = ""
                    } else {
                        btnFavorite.setImageResource(android.R.drawable.btn_star_big_on)
                        it.tag = "fav"
                    }
                }

                val isNew = System.currentTimeMillis() - listing.createdAt < TimeUnit.DAYS.toMillis(1)
                chipNewBadge.visibility = if (isNew) View.VISIBLE else View.GONE
                
                ivListingImage.load(listing.imageUrl) {
                    crossfade(true)
                    placeholder(android.R.color.darker_gray)
                    error(android.R.color.darker_gray)
                    precision(Precision.INEXACT)
                    diskCachePolicy(CachePolicy.ENABLED)
                    memoryCachePolicy(CachePolicy.ENABLED)
                }

                ivListingImage.transitionName = "listing_image_${listing.id}"
                root.setOnClickListener { onItemClick(listing, ivListingImage) }
            }
        }

        private fun setupDealBadge(price: Double, category: String) {
            val (label, colorStateList) = when {
                category == "Mobiles" && price < 100000 -> "Great Deal" to colorGreatDeal
                category == "Mobiles" && price > 130000 -> "High Price" to colorHighPrice
                category == "Vehicles" && price < 150000 -> "Great Deal" to colorGreatDeal
                else -> "Fair Price" to colorFairPrice
            }
            
            binding.chipDealBadge.text = label
            binding.chipDealBadge.chipBackgroundColor = colorStateList
        }

        private fun formatPrice(price: Double): String {
            return priceFormatter.format(price).replace("PKR", "Rs. ")
        }
    }

    class ListingDiffCallback : DiffUtil.ItemCallback<Listing>() {
        override fun areItemsTheSame(oldItem: Listing, newItem: Listing): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Listing, newItem: Listing): Boolean {
            return oldItem == newItem
        }
    }
}
