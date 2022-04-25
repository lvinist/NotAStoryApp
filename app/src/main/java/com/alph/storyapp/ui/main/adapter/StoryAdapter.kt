package com.alph.storyapp.ui.main.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alph.storyapp.data.Story
import com.alph.storyapp.databinding.ItemRowPhotoBinding
import com.alph.storyapp.ui.detail.StoryDetailActivity

class StoryAdapter : PagingDataAdapter<Story, StoryAdapter.StoryViewHolder>(Comparator) {

    private object Comparator : DiffUtil.ItemCallback<Story>() {
        override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean =
            oldItem.description == newItem.description
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(getItem(position) ?: Story())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder = StoryViewHolder(
        binding = ItemRowPhotoBinding.inflate(LayoutInflater.from(parent.context),
        parent,
        false
        )
    )

    inner class StoryViewHolder(
        private val binding: ItemRowPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: Story) {
            binding.story = story

            itemView.setOnClickListener{
                val intent = Intent(itemView.context, StoryDetailActivity::class.java)
                intent.putExtra("Story", story)

                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity,
                    Pair(binding.ivPhoto, "image"),
                    Pair(binding.tvName, "name"),
                    Pair(binding.tvDescription, "description")
                ).toBundle()

                itemView.context.startActivity(intent, optionsCompat)
            }
        }
    }

}