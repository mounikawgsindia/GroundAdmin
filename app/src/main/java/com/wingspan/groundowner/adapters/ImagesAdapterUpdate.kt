package com.wingspan.groundowner.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wingspan.groundowner.R
import com.wingspan.groundowner.databinding.CustomGroundImagesBinding


class ImagesAdapterUpdate(
    val context: Context, var imagesList: ArrayList<Uri>, var imageNamesList: ArrayList<String>
):
    RecyclerView.Adapter<ImagesAdapterUpdate.ImagesViewHolder>() {


    interface OnItemRemovedListener1 {
        fun onItemRemoved1(isEmpty: Boolean,imageType:String)
        {

        }
    }

    class ImagesViewHolder(var binding: CustomGroundImagesBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val binding= CustomGroundImagesBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return ImagesViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return imagesList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        holder.binding.apply {

            closeImage.setOnClickListener {
                showDeleteAlertDialog(position)
            }
            //imageIV.setImageURI(imagesList[position])
            val imageUrl = imagesList[position]


            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.gallery1) // Placeholder while loading
                .error(R.drawable.gallery1)
                .into(imageIV)

            imagenameet.setText(imageNamesList[position])
            // Handle Image name changes
            imagenameet.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(editable: Editable?) {
                    // Save the updated image name to the imageNamesList
                    imageNamesList[position] = editable.toString()
                }
            })
        }
    }
    // Method to remove an item from the list
    private fun removeItem(position: Int) {
        imagesList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, imagesList.size)
        //listener.onItemRemoved1(imagesList.isEmpty(),imageType)
    }

    private fun showDeleteAlertDialog(position:Int){
        val bundle= AlertDialog.Builder(context)
        bundle.setMessage("Do you want to delete Image")
        bundle.setPositiveButton("Yes") { dialog, _ ->
            removeItem(position)
        }
        bundle.setNegativeButton("NO") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog=bundle.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

}
