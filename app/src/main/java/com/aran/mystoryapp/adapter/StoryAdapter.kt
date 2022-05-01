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
import androidx.recyclerview.widget.RecyclerView
import com.aran.mystoryapp.R
import com.aran.mystoryapp.model.StoryModel
import com.aran.mystoryapp.ui.DetailStoryActivity
import com.bumptech.glide.Glide

class StoryAdapter(private val listStories: ArrayList<StoryModel>) : RecyclerView.Adapter<StoryAdapter.ViewHolder>(){
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.image)
        val name: TextView = view.findViewById(R.id.name)
        val description: TextView = view.findViewById(R.id.description)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.stories_item, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.name.text = listStories[position].name
        viewHolder.description.text = listStories[position].description
        Glide.with(viewHolder.itemView.context)
            .load(listStories[position].image)
            .into(viewHolder.img)

        viewHolder.itemView.setOnClickListener {
            val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                viewHolder.itemView.context as Activity,
                Pair(viewHolder.img, "img_photo_detail_transition"),
                Pair(viewHolder.name, "name_detail_transition"),
                Pair(viewHolder.description, "description_detail_transition"),
            )

            val intent = Intent(viewHolder.itemView.context, DetailStoryActivity::class.java)
            intent.putExtra(DetailStoryActivity.DETAIL_STORY, listStories[position])
            viewHolder.itemView.context.startActivity(intent, optionsCompat.toBundle())
        }
    }

    override fun getItemCount(): Int {
        return listStories.size
    }
}