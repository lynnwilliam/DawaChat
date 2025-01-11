package com.dawaluma.desisionmaker

import android.content.Context

object AppInfoProvider {

    fun getAppVersionDetails(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "Version: ${packageInfo.versionName} (Code: ${packageInfo.versionCode})"
        } catch (e: Exception) {
            "Version: Unknown"
        }
    }
}