package com.wingspan.groundowner.utils

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserPreferences @Inject constructor (@ApplicationContext private val  context: Context) {
    val sharedPreferences =context.getSharedPreferences("storedata",Context.MODE_PRIVATE)

    companion object {
        const val KEY_Address = "address"
    }
    @SuppressLint("CommitPrefEdits")
    fun saveAddress(address:String?){
        sharedPreferences.edit().apply(){
            putString(KEY_Address,address)
            apply()
        }
    }


    fun getAddress(): String? {
        return sharedPreferences.getString(KEY_Address,null)
    }
}