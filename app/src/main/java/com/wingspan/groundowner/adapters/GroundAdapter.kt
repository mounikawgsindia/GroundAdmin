package com.wingspan.groundowner.adapters

import GetGround

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wingspan.groundowner.R
import com.wingspan.groundowner.activities.MainActivity
import com.wingspan.groundowner.databinding.CustomAddGroundBinding
import com.wingspan.groundowner.fragments.GroundsFragemntDirections
import com.wingspan.groundowner.utils.Singleton.setDebouncedClickListener
import com.wingspan.groundowner.utils.UserPreferences
import com.wingspan.groundowner.viewmodel.GroundsViewModel

class GroundAdapter(val context: Context, var groundsList:ArrayList<GetGround>, private val viewModel: GroundsViewModel,private val navController: NavController):
    RecyclerView.Adapter<GroundAdapter.ViewHolder> () {

    lateinit var alertDialog:AlertDialog
    class ViewHolder(var binding: CustomAddGroundBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val root =
            CustomAddGroundBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(root)
    }

    override fun getItemCount(): Int {
        return groundsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val listData = groundsList.get(position)
            groundName.text=listData.groundHeading
            address?.text = listData.groundAddress
            facilities.text=listData.facilities
            cost.text=listData.pricePerHour.toString()
            sportsType.text=listData.sportsType


            // Set up RecyclerView for images
            imagesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            //imagesRecyclerView.adapter = ImageAdapter(listData.imageUrls, listData.groundHeading)
//            rightArrow.setDebouncedClickListener() {
//                alertDialogUpdate(listData.clgId, listData.clgName)
//            }
            btnDelete.setDebouncedClickListener(){
                deleteDialog(listData.id)
            }
            rowLl.setDebouncedClickListener {

                val bundle = Bundle().apply {
                    putSerializable("ground_key", listData)
                }
                navController.navigate(R.id.action_groundfragment_to_displayFragment,bundle)

            }

        }
    }

    fun update(newData:List<GetGround>){
        groundsList= newData as ArrayList<GetGround>
        notifyDataSetChanged()
    }
    fun addItem(newData :GetGround){
        groundsList.add(newData)
        notifyDataSetChanged()
    }
    fun deleteItem(id: Int){
        val index= groundsList.indexOfFirst { it.id == id }
        if(index!=-1){
            groundsList.removeAt(index)
            notifyItemRemoved(index)
        }
    }
    private fun deleteDialog(id: Int) {

        val builder= AlertDialog.Builder(context)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure want to Delete?")
        builder.setPositiveButton("OK"){dialog,which->
            viewModel.deleteGround(id)
            dialog.dismiss()
        }
        builder.setNegativeButton("CANCEL"){dialog,which->
            dialog.dismiss()
        }
        alertDialog=builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

}