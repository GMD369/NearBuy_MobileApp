package com.nearbuy.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.nearbuy.app.databinding.ItemImageCarouselBinding
import java.io.File

class ImageCarouselAdapter(private val imagePaths: List<String>) :
    RecyclerView.Adapter<ImageCarouselAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageCarouselBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) =
        holder.bind(imagePaths[position])

    override fun getItemCount() = imagePaths.size

    class ImageViewHolder(private val binding: ItemImageCarouselBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(path: String) {
            binding.imageView.load(File(path)) {
                crossfade(true)
            }
        }
    }
}
