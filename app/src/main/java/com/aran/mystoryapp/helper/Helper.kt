package com.aran.mystoryapp.helper

import android.content.Context
import android.content.SharedPreferences

class Helper (context: Context){
    private val PREF = "loginData"
    private val pref : SharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
    val editor : SharedPreferences.Editor = pref.edit()

    fun clear(){
        editor.clear().apply()
    }

    fun getToken(key: String) : String? {
        return  pref.getString(key, null)
    }
}