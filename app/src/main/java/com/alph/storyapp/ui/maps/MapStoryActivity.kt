package com.alph.storyapp.ui.maps

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.alph.storyapp.R
import com.alph.storyapp.databinding.ActivityMapStoryBinding
import com.alph.storyapp.storage.UserPreference
import com.alph.storyapp.ui.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MapStoryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapStoryBinding

    private lateinit var mapStoryViewModel: MapStoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        initView()

    }

    fun initView() {
        val pref = UserPreference.getInstance(dataStore)
        mapStoryViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MapStoryViewModel::class.java]

        mapStoryViewModel.getUser().observe(this){ user ->
            mapStoryViewModel.setStoriesWithLocation(tokenAuth = user.token)
        }

        mapStoryViewModel.getStoriesWithLocation().observe(this) {stories ->
            val storiesLatLng = stories?.map { LatLng((it.lat ?: 0) as Double, (it.lon ?: 0) as Double) }
            if (storiesLatLng!!.isNotEmpty()) {
                storiesLatLng.forEachIndexed { index, latlng ->
                    mMap.addMarker(MarkerOptions().position(latlng).title(stories[index].name))
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(storiesLatLng[1]))
            }
        }
    }
}