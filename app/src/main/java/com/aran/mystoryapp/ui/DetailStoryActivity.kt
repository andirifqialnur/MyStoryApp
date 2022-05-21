package com.aran.mystoryapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aran.mystoryapp.databinding.ActivityDetailStoryBinding
import com.aran.mystoryapp.model.StoryModel
import com.bumptech.glide.Glide

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<StoryModel>(DETAIL_STORY) as StoryModel
        Glide.with(this)
            .load(story.image)
            .into(binding.img)
        binding.name.text = story.name
        binding.description.text = story.description
    }

    companion object {
        const val DETAIL_STORY = "detail_story"
    }
}