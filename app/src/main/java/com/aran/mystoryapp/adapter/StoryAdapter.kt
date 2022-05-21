package com.aran.mystoryapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aran.mystoryapp.R
import com.aran.mystoryapp.databinding.StoriesItemBinding
import com.aran.mystoryapp.model.StoryModel
import com.aran.mystoryapp.response.ListStoryItem
import com.aran.mystoryapp.ui.DetailStoryActivity
import com.bumptech.glide.Glide

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(private val binding: StoriesItemBinding) :RecyclerView.ViewHolder(binding.root){
        fun bind(data: ListStoryItem) {
            Glide.with(binding.root.context)
                .load(data.photoUrl)
                .into(binding.image)
            binding.name.text = data.name

            binding.root.setOnClickListener {
                val story = StoryModel(
                    data.name,
                    data.photoUrl,
                    data.description,
                    null,
                    null
                )

                val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    binding.root.context as Activity,
                    Pair(binding.image, "img_photo_detail_transition"),
                    Pair(binding.name, "tv_name_detail_transition")
                )

                val intent = Intent(binding.root.context, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.DETAIL_STORY, story)
                binding.root.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StoriesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if(data != null) {
            holder.bind(data)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}