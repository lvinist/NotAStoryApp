package com.alph.storyapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alph.storyapp.R
import com.alph.storyapp.data.Story
import com.alph.storyapp.databinding.ActivityStoryDetailBinding
import com.alph.storyapp.helper.Helper.withDateFormat
import com.bumptech.glide.Glide

class StoryDetailActivity : AppCompatActivity() {


    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        setupView()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupView() {
        val story = intent.getParcelableExtra<Story>("Story") as Story
        binding.nameTextView.text = story.name
        binding.dateTextView.text = getString(R.string.date, story.createdAt.withDateFormat())
        binding.descTextView.text = story.description

        Glide.with(this)
            .load(story.photoUrl)
            .into(binding.previewImageView)
    }
}