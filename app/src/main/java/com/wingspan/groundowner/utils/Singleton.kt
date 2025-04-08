package com.wingspan.groundowner.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.wingspan.groundowner.R
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


object Singleton {

    fun View.setDebouncedClickListener(debounceTimeMillis:Long=500, onClick:()->Unit){
        val handler= Handler(Looper.getMainLooper())
        var lastClickTime=0L
        setOnClickListener {
            val currentTime= System.currentTimeMillis()
            if(currentTime-lastClickTime>=debounceTimeMillis){
                lastClickTime=currentTime
                handler.post(){
                    onClick()
                }
            }
        }
    }
    @SuppressLint("MissingPermission")
    fun isNetworkAvailable(context: Context):Boolean
    {
        val connectivityManager=context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }
    @SuppressLint("MissingInflatedId", "InflateParams")
    fun showNetworkAlertDialog(context: Context){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout=inflater.inflate(R.layout.alertdialog_network_connection,null)
        var dialog= AlertDialog.Builder(context).setView(layout).setCancelable(false).create()
        val close=layout.findViewById<AppCompatButton>(R.id.close)
        close.setOnClickListener(){
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.white_bg))
        dialog.show()

    }
    fun hideKeyboard(contecxt:Context, view: View?) {
        val imm = contecxt.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view?.let{
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

    }
    fun showToast(context:Context,message:String)
    {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }
    @SuppressLint("RestrictedApi", "MissingInflatedId")
    fun showCustomSnackbar(
        context: Context,
        message: String,color:Int
    ) {
        val activity = context as? Activity ?: return

        // Fetch the root view of the activity
        val rootView = activity.findViewById<View>(android.R.id.content)
        val snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_LONG)

        // Inflate custom view
        val customSnackbarView = LayoutInflater.from(context).inflate(R.layout.customised_snackbar, null)

        // Customize the layout elements inside customSnackbarView
        val errorMessage = customSnackbarView.findViewById<TextView>(R.id.snackbar_text)
        val viewColor = customSnackbarView.findViewById<View>(R.id.view)
        errorMessage.text = message
        Log.d("view color","view Color ${color}")
        val actualColor = ContextCompat.getColor(context, color)
        viewColor.setBackgroundColor(actualColor)
        // Add custom view to Snackbar
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        snackbarLayout.setPadding(0, 0, 0, 0) // Remove default padding
        snackbarLayout.addView(customSnackbarView, 0)
        snackbarLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        snackbar.show()
    }
}