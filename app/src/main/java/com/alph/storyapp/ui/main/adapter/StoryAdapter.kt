package com.alph.storyapp.ui.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alph.storyapp.data.Story
import com.alph.storyapp.databinding.ItemRowPhotoBinding
import com.alph.storyapp.ui.main.MainActivity
import com.bumptech.glide.Glide

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    private var storyList: List<Story>? = null
    private var onItemClickCallback: OnItemClickCallback? = null


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setStoryList(storyList: List<Story>?) {
        this.storyList = storyList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = ItemRowPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(storyList?.get(position)!!)
    }

    override fun getItemCount(): Int {
        if(storyList == null) return 0
        else return storyList?.size!!
    }

    inner class StoryViewHolder(private val binding: ItemRowPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Story) {

            binding.root.setOnClickListener{
                onItemClickCallback?.onItemClicked(data)
            }

            binding.apply {
                tvName.text = data.name
                tvDescription.text = data.description

                Glide.with(itemView)
                    .load(data.photoUrl)
                    .into(ivPhoto)
            }

        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Story)
    }

}