package com.dawaluma.desisionmaker

import android.speech.tts.UtteranceProgressListener
import com.dawaluma.desisionmaker.database.DataType
import com.dawaluma.desisionmaker.webapi.youtube.LatLong

interface SpeakText {
    public fun speakText(text: String, speechListener: UtteranceProgressListener?)
    public fun isSpeakSupported(): Boolean
}

interface IntentLauncher {
    fun openMapWithAddress(address: String)
    fun requestLocationPermissions()
    fun openVideo(video: DataType.Video)
    fun openBrowser(address: String)
    fun getDeviceLocation(): LatLong?
}