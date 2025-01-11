package com.dawaluma.desisionmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dawaluma.desisionmaker.MainActivity.Location.latitude
import com.dawaluma.desisionmaker.MainActivity.Location.longitude
import com.dawaluma.desisionmaker.database.APIKeys
import com.dawaluma.desisionmaker.database.AppSettingsContract
import com.dawaluma.desisionmaker.database.DataType
import com.dawaluma.desisionmaker.screens.MainLayout
import com.dawaluma.desisionmaker.webapi.youtube.LatLong
import com.google.android.gms.location.*
import java.util.*

class MainActivity : ComponentActivity(),  TextToSpeech.OnInitListener, SpeakText, IntentLauncher
{
    private lateinit var tts: TextToSpeech
    private var ttsWorking: Boolean = false
    private var fusedLocationClient: FusedLocationProviderClient ? = null
    private lateinit var appSettings: AppSettingsContract

    private object Location{
        var latitude: Double = -1.0
        var longitude: Double = -1.0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appSettings = (application as DawaApplication).appSettings
        tts = TextToSpeech(this, this)
        APIKeys.loadAPIKeysFile(applicationContext)

        if ( appSettings.hasMapsAPI()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        }

        setContent {
            DesisionMakerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainLayout(this, this, appSettings)
                }
            }
        }

        if (isPermissionsGranted()) {
            onPermissionsGranted()  // If permissions are granted, call your function
        } else {
            // If not granted, request permissions
            if ( appSettings.hasMapsAPI()) {
                requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
            }
        }

        if ( areAllPermissionsGranted()){
            getLastKnownLocation()
        }
    }

    private fun isPermissionsGranted(): Boolean{
        return  REQUIRED_PERMISSIONS.all {
            checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
        }
    }

    // This method is called when TTS is initialized
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language and check if it's supported
            val result = tts.setLanguage(Locale.US)
            ttsWorking =
                !(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
        } else {
            ttsWorking=false
        }
    }

    override fun onDestroy() {
        // Shutdown TTS when not needed
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    override fun speakText(text: String, speechListener: UtteranceProgressListener ?) {
        if ( tts.isSpeaking){
            tts.stop()
        }else {

            speechListener?. let{
                tts.setOnUtteranceProgressListener(speechListener)
            }
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    override fun isSpeakSupported(): Boolean {
       return ttsWorking
    }


    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_WIFI_STATE
    )

    private val REQUEST_CODE_PERMISSIONS = 1001

    private fun checkAndRequestPermissions() {
        val missingPermissions = REQUIRED_PERMISSIONS.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), REQUEST_CODE_PERMISSIONS)
        } else {
            // All permissions are already granted
            onPermissionsGranted()
        }
    }

    private fun areAllPermissionsGranted(): Boolean{
        val missingPermissions = REQUIRED_PERMISSIONS.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }
        return missingPermissions.isEmpty()
    }

    // Register the launcher for multiple permissions
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Check if all permissions are granted
            if (permissions.values.all { it }) {
                onPermissionsGranted()  // All permissions granted
            } else {
                onPermissionsDenied()  // Some or all permissions denied
            }
        }

    private fun onPermissionsGranted() {
        getLastKnownLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {

        fusedLocationClient?.let{
            it.requestLocationUpdates(
                LocationRequest.create(),
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val location = locationResult.lastLocation
                        location?.let { loc ->
                            latitude = loc.latitude
                            longitude = loc.longitude
                        }
                        it.removeLocationUpdates(this) // Stop updates after receiving the result
                    }
                },
                Looper.getMainLooper()
            )
        }
    }

    private fun onPermissionsDenied() {

    }

    override fun openMapWithAddress(address: String) {
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")

        // Create an implicit intent with the geo URI
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        startActivity(mapIntent)
    }

    override fun requestLocationPermissions() {
        checkAndRequestPermissions()
    }

    override fun openVideo(video: DataType.Video) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=${video.videoID}")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK // Add this flag
        }
        applicationContext.startActivity(intent)
    }

    override fun openBrowser(address: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(address)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        applicationContext.startActivity(intent)
    }

    override fun getDeviceLocation(): LatLong? {
        if (isPermissionsGranted()) {
            return LatLong(Location.latitude, Location.longitude)
        }
        return null
    }
}