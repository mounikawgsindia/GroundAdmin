package com.wingspan.groundowner.utils

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserPreferences @Inject constructor (@ApplicationContext private val  context: Context) {
    val sharedPreferences =context.getSharedPreferences("storedata",Context.MODE_PRIVATE)

    companion object {
        const val KEY_ID = "id"
        const val KEY_NAME = "name"
        const val KEY_EMAIL = "email"
        const val KEY_NUMBER = "number"
        const val KEY_TOKEN = "token"
        const val KEY_ADDRESS = "address"
        const val KEY_ABOUT = "about"
        const val KEY_PRICE = "price"
        const val KEY_SLOTS = "slots"
        const val KEY_specialization = "specialization"
        const val KEY_profileImage = "image"
        const val KEY_gallery = "gallery"
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
    fun saveTrainerData(
        id: String?,
        token: String?,
        username: String?,
        email: String?,
        mobilenumber: String?,
        specialization: String?,
        profileImage: String?,
        gallery: List<String?>,
        address: String?,
        about: String?,
        price: String?,
        slots: String?
    ) {
        sharedPreferences.edit().apply {
            putString(KEY_ID, id)
            putString(KEY_TOKEN, token)
            putString(KEY_NAME, username)
            putString(KEY_EMAIL, email)
            putString(KEY_NUMBER, mobilenumber)
            putString(KEY_specialization, specialization)
            putString(KEY_profileImage, profileImage)
            putString(KEY_gallery, gallery.toString())
            putString(KEY_ADDRESS, address)
            putString(KEY_ABOUT, about)
            putString(KEY_PRICE, price)
            putString(KEY_SLOTS, slots)
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
    fun getSpecialization(): String? {
        return sharedPreferences.getString(KEY_specialization, null)
    }
    fun getAddress(): String? {
        return sharedPreferences.getString(KEY_ADDRESS, null)
    }
    fun getAbout(): String? {
        return sharedPreferences.getString(KEY_ABOUT, null)
    }
    fun getPrice(): String? {
        return sharedPreferences.getString(KEY_PRICE, null)
    }

    fun getProfileImage(): String? {
        return sharedPreferences.getString(KEY_profileImage, null)
    }

    // If gallery is stored as a string list (e.g., "url1, url2")
    fun getGallery(): List<String> {
        val galleryString = sharedPreferences.getString(KEY_gallery, null) ?: return emptyList()
        return galleryString
            .removePrefix("[")
            .removeSuffix("]")
            .split(", ")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
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
    fun getSlots(): String? {
        return sharedPreferences.getString(KEY_SLOTS, null)
    }

    fun getEmail(): String? {
        return sharedPreferences.getString(KEY_EMAIL, null)
    }

    fun getMobileNumber(): String? {
        return sharedPreferences.getString(KEY_NUMBER, null)
    }
    fun isLoggedIn(): Boolean = sharedPreferences.getBoolean(IS_LOGGED_IN, false)
}