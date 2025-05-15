package com.wingspan.groundowner.adapters

import com.wingspan.groundowner.model.GetGround
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wingspan.groundowner.R
import com.wingspan.groundowner.databinding.CustomIntroGroundBinding


class IntroSlidesAdapter(private val context: Context, private val imagesList: List<String>, private val navController: NavController, private val groundData: GetGround) :
    RecyclerView.Adapter<IntroSlidesAdapter.SportsViewHolder>() {

    class SportsViewHolder(var binding: CustomIntroGroundBinding) : RecyclerView.ViewHolder(binding.root) {

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportsViewHolder {
        val binding = CustomIntroGroundBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SportsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SportsViewHolder, position: Int) {
        val imageUrl=imagesList[position]
        holder.binding.apply {
            // Load the image URL into the ImageView (assuming there's an ImageView in the layout)
            Glide.with(context)
                .load(imageUrl)
                .into(mainGroundImage) // Replace with the actual ImageView id from your layout
// Set the click listener to handle image tap
            holder.binding.root.setOnClickListener {
                val bundle = Bundle().apply {
                    putSerializable("ground_key", groundData)
                }
                navController.navigate(R.id.action_groundfragment_to_displayFragment, bundle)
            }
        }
    }

    override fun getItemCount(): Int = imagesList.size
}