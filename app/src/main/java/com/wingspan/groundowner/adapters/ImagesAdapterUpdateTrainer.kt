package com.wingspan.groundowner.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wingspan.groundowner.R
import com.wingspan.groundowner.databinding.CustomGroundImagesBinding

class ImagesAdapterUpdateTrainer(
    val context: Context, var imagesList: ArrayList<Uri>
) : RecyclerView.Adapter<ImagesAdapterUpdateTrainer.ImagesViewHolder>() {



    class ImagesViewHolder(var binding: CustomGroundImagesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val binding = CustomGroundImagesBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImagesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        holder.binding.apply {
            imagenameet.visibility= View.GONE
            closeImage.setOnClickListener {
                showDeleteAlertDialog(position)
            }

            val imageUrl = imagesList[position]

            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.gallery1)
                .error(R.drawable.gallery1)
                .into(imageIV)


        }
    }

    private fun removeItem(position: Int) {
        imagesList.removeAt(position)

        notifyItemRemoved(position)
        notifyItemRangeChanged(position, imagesList.size)


    }

    private fun showDeleteAlertDialog(position: Int) {
        val bundle = AlertDialog.Builder(context)
        bundle.setMessage("Do you want to delete Image")
        bundle.setPositiveButton("Yes") { dialog, _ ->
            removeItem(position)
        }
        bundle.setNegativeButton("NO") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = bundle.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}