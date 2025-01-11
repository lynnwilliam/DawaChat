package com.dawaluma.desisionmaker.database

import android.content.Context
import android.content.SharedPreferences

    fun getIDMaker(context: Context): IncrementalNumbersInterface {
        return AppIDMaker(context)
    }

    private class AppIDMaker(private val context: Context): IncrementalNumbersInterface {

        override fun getNewID(): Long {
           return getNewID("App")
        }

        override fun getNewID(type: String): Long {
            // Access SharedPreferences (Use a unique name for your app's shared preferences)
            val sharedPref: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val nextNumber = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getLong(type, 0) + 1

            with(sharedPref.edit()) {
                putLong(type, nextNumber)
                apply()
            }
            return nextNumber
        }

    }
