package com.nearbuy.app.ui.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.nearbuy.app.databinding.ItemSelectedImageBinding
import java.io.File

class SelectedImageAdapter(
    private val images: MutableList<String>,
    private val onRemove: (Int) -> Unit
) : RecyclerView.Adapter<SelectedImageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSelectedImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(path: String, position: Int) {
            binding.ivThumb.load(File(path)) {
                crossfade(true)
                error(android.R.color.darker_gray)
            }
            binding.btnRemove.setOnClickListener { onRemove(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemSelectedImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(images[position], position)

    override fun getItemCount() = images.size
}
