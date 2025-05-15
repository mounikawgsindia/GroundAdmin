package com.wingspan.groundowner.utils

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserPreferences @Inject constructor (@ApplicationContext private val  context: Context) {
    val sharedPreferences =context.getSharedPreferences("storedata",Context.MODE_PRIVATE)

    companion object {

        const val KEY_NAME = "name"
        const val KEY_EMAIL = "email"
        const val KEY_NUMBER = "number"
        const val KEY_TOKEN = "token"
        const val IS_LOGGED_IN="isLoggedIn"
        const val USER_TYPE="usertype"
    }
    fun saveUser(userType:String?){
        sharedPreferences.edit().apply(){
            putString(USER_TYPE,userType)
            apply()
        }
    }

    fun saveData(token:String?,username:String?,email:String?,mobilenumber:String?){
        sharedPreferences.edit().apply(){
            putString(KEY_TOKEN, token)
            putString(KEY_NAME, username)
            putString(KEY_EMAIL, email)
            putString(KEY_NUMBER, mobilenumber)
            putBoolean(IS_LOGGED_IN, true)
            apply()
        }
    }
    @SuppressLint("CommitPrefEdits")
    fun logoutAdmin(){
        sharedPreferences.edit().apply {
            putBoolean(IS_LOGGED_IN, false)
            apply() // Save changes
        }

    }
    fun getUserType():String?{
        return sharedPreferences.getString(USER_TYPE, null)
    }
    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun getUsername(): String? {
        return sharedPreferences.getString(KEY_NAME, null)
    }

    fun getEmail(): String? {
        return sharedPreferences.getString(KEY_EMAIL, null)
    }

    fun getMobileNumber(): String? {
        return sharedPreferences.getString(KEY_NUMBER, null)
    }
    fun isLoggedIn(): Boolean = sharedPreferences.getBoolean(IS_LOGGED_IN, false)
}