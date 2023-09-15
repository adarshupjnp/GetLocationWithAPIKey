package com.example.getlocation

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Check for location permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Get the last known location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    // Get the current address from the location
                    val address = getAddressFromLocation(it)

                    // Display the address on a TextView (replace with your TextView)
                     val addressTextView = findViewById<TextView>(R.id.addressTextView)
                     addressTextView.text = address

                    // Create a LatLng object from the location
                    val currentLatLng = LatLng(it.latitude, it.longitude)

                    // Add a marker on the map
                    mMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))

                    // Move the camera to the current location
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
    }

    private fun getAddressFromLocation(location: Location): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)!!.filterNotNull()
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                return address.getAddressLine(0) // Return the first address line
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "Address not found"
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
