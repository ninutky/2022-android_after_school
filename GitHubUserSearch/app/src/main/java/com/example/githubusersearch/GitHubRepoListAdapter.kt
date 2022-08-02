package com.example.githubusersearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GitHubRepoListAdapter(val dataList: List<GitHubRepo>)
    : RecyclerView.Adapter<GitHubRepoListAdapter.ItemViewHolder>()
{
    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.view.findViewById<TextView>(R.id.repo_name).text = dataList[position].name
        holder.view.findViewById<TextView>(R.id.repo_description).text = dataList[position].description
        holder.view.findViewById<TextView>(R.id.repo_forks_count).text = dataList[position].forks_count.toString()
        holder.view.findViewById<TextView>(R.id.repo_watchers_count).text = dataList[position].watchers_count.toString()
        holder.view.findViewById<TextView>(R.id.repo_stargazers_count).text = dataList[position].stargazers_count.toString()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.repo_item
    }
}