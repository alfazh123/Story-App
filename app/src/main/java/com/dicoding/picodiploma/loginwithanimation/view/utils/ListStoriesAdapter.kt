package com.dicoding.picodiploma.loginwithanimation.view.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ListStoriesBinding

class ListStoriesAdapter: PagingDataAdapter<ListStoryItem, ListStoriesAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    private lateinit var onStoryClickCallback: OnItemClickback

    interface OnItemClickback {
        fun onItemClicked(story: ListStoryItem)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickback) {
        this.onStoryClickCallback = onItemClickCallback
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var binding = ListStoriesBinding.bind(itemView)

        fun bind(story: ListStoryItem) {
            with(binding) {
                tvItemName.text = story.name
                tvItemDescription.text = story.description
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(ivItemPhoto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = LayoutInflater.from(parent.context).inflate(R.layout.list_stories, parent, false)

        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item!!)

        holder.itemView.setOnClickListener {
            onStoryClickCallback.onItemClicked(item)
        }
    }
}