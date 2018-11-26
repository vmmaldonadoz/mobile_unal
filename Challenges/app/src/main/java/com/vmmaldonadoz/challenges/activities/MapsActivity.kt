package com.vmmaldonadoz.challenges.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.vmmaldonadoz.challenges.R
import com.vmmaldonadoz.challenges.databinding.ActivityMapsBinding
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.concurrent.TimeUnit

class MapsActivity : AppCompatActivity(),
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    private val MY_PERMISSIONS_REQUEST_LOCATION: Int = 0x1

    private val DEFAULT_ZOOM = 16f

    private lateinit var googleMap: GoogleMap

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private lateinit var locationCallback: LocationCallback

    private var requestingLocationUpdates: Boolean = true

    private val locationRequest = LocationRequest().apply {
        interval = TimeUnit.SECONDS.toMillis(10)
        fastestInterval = TimeUnit.SECONDS.toMillis(5)
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }


    private val binding: ActivityMapsBinding by lazy {
        DataBindingUtil.setContentView<ActivityMapsBinding>(this,
                R.layout.activity_maps)
    }

    private val geoDataClient: GeoDataClient by lazy { Places.getGeoDataClient(this) }
    private val placeDetectionClient: PlaceDetectionClient by lazy { Places.getPlaceDetectionClient(this) }

    private var mLastKnownLocation: LatLng? = null

    private val MAX_ENTRIES = 5
    private var mLikelyPlaceNames = arrayListOf<String>()
    private var mLikelyPlaceAddresses = arrayListOf<String>()
    private var mLikelyPlaceAttributions = arrayListOf<String>()
    private var mLikelyPlaceLatLngs = arrayListOf<LatLng>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root

        setupMap()
        setupLocation()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        googleMap.uiSettings.apply {
            isMyLocationButtonEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = false
        }


    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private var userMarker: Marker? = null

    private fun setupLocation() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                var locations = ""
                for (location in locationResult.locations) {
                    locations += "Location: (${location.latitude},${location.longitude}). Accuracy: ${location.accuracy}\n"
                }
                binding.message.text = locations
                googleMap.let { map ->
                    val latLng = LatLng(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
                    mLastKnownLocation = latLng
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
                    userMarker?.remove()
                    userMarker = map.addMarker(
                            MarkerOptions()
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                    .position(latLng)
                    )
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_maps, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.option_get_place -> showCurrentPlace()
        }

        return true
    }

    @SuppressLint("MissingPermission")
    private fun showCurrentPlace() {
        val placeResult = placeDetectionClient.getCurrentPlace(null)
        placeResult.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                val likelyPlaces = task.result!!

                val count = if (likelyPlaces.count < MAX_ENTRIES) {
                    likelyPlaces.count
                } else {
                    MAX_ENTRIES
                }

                var counter = 1
                likelyPlaces.forEach { placeLikelihood ->
                    val place = placeLikelihood.place
                    mLikelyPlaceNames.add(place.name.toString())
                    mLikelyPlaceAddresses.add(place.address.toString())
                    mLikelyPlaceAttributions.add(place.attributions.orEmpty())
                    mLikelyPlaceLatLngs.add(place.latLng)

                    if (++counter == count) {
                        return@forEach
                    }
                }

                likelyPlaces.release()


                openPlacesDialog()
            }
        }

    }

    private fun openPlacesDialog() {
        val listener = DialogInterface.OnClickListener { _, which ->
            val markerLatLng = mLikelyPlaceLatLngs[which]
            val markerSnippet = "${mLikelyPlaceAddresses[which]}\n${mLikelyPlaceAttributions[which]}"

            googleMap.addMarker(
                    MarkerOptions()
                            .title(mLikelyPlaceNames.get(which))
                            .position(markerLatLng)
                            .snippet(markerSnippet)
            )

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng, DEFAULT_ZOOM))
        }

        AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames.toTypedArray(), listener)
                .show()
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_LOCATION)
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_LOCATION)
            }
        } else {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    null)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startLocationUpdates()
                } else {
                    finish()
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
            else -> {
            }
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG).show()
    }
}

private fun CharSequence?.orEmpty(): String {
    return if (isNullOrBlank()) {
        ""
    } else {
        toString()
    }
}
