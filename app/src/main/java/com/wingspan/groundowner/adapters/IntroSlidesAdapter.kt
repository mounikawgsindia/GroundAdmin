package com.wingspan.groundowner.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wingspan.groundowner.R
import com.wingspan.groundowner.databinding.ItemIntroSlideBinding


class IntroSlidesAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<IntroSlidesAdapter.SportsViewHolder>() {

    inner class SportsViewHolder(binding: ItemIntroSlideBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportsViewHolder {
        val binding = ItemIntroSlideBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SportsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SportsViewHolder, position: Int) {

    }

    override fun getItemCount(): Int = imageUrls.size
}