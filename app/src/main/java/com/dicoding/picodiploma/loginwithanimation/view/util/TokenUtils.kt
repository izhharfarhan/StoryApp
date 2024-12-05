package com.dicoding.picodiploma.loginwithanimation.view.util

import android.content.Context
import android.content.SharedPreferences

object TokenUtils {

    private const val PREF_NAME = "user_preferences"
    private const val KEY_TOKEN = "access_token"

    // Menyimpan token ke SharedPreferences
    fun saveToken(context: Context, token: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_TOKEN, token)
        editor.apply()
    }

    // Mendapatkan token dari SharedPreferences
    fun getToken(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_TOKEN, null) // Mengembalikan null jika token tidak ada
    }

    // Menghapus token dari SharedPreferences
    fun clearToken(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(KEY_TOKEN)
        editor.apply()
    }
}
