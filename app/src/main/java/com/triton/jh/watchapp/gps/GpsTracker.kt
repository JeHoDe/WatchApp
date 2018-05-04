package com.triton.jh.watchapp.gps

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.IBinder
import android.location.LocationManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log


class GpsTracker : Service() {

    companion object {
        private const val TAG = "GpsTracker"
        private const val LOCATION_INTERVAL = 10000
        private const val LOCATION_DISTANCE = 0f

        private var mLocationManager: LocationManager? = null

        var avgSpeed = 0F
        var maxSpeed = 0F

        var distanceInMetres = 0
        var speed = 0F
        var altitude = 0
        var signal = false

        var lastLocation: Location? = null

        private var lastUpdate = 0L
        private var secondsRunning = 0L

        fun reset() {

            speed = 0F
            altitude = 0
            lastLocation = null

            lastUpdate = 0L
            secondsRunning = 0L
        }
    }

    private inner class LocationListener(provider: String) : android.location.LocationListener {

        init {
            Log.i(TAG, "LocationListener $provider")
        }

        override fun onLocationChanged(location: Location) {
            Log.i(TAG, "onLocationChanged: $location")

            signal = !(location == null || (location.hasAccuracy() && location.accuracy > 30))

            if (location != null) {

                if (lastLocation != null) {
                    distanceInMetres += location.distanceTo(lastLocation).toInt()
                }

                speed = (location.speed * 60 * 60) / 1000F
                altitude = location.altitude.toInt()

                val now = SystemClock.uptimeMillis() / 1000
                if (lastUpdate > 0)
                {
                    val diff = now - lastUpdate
                    avgSpeed = ((avgSpeed * secondsRunning) + speed) / (secondsRunning + diff)
                    secondsRunning += diff
                }
                lastUpdate = now

                if (speed > maxSpeed) {
                    maxSpeed = speed
                }
            }

            lastLocation = location
        }

        override fun onProviderDisabled(provider: String) {
            Log.i(TAG, "onProviderDisabled: $provider")
        }

        override fun onProviderEnabled(provider: String) {
            Log.i(TAG, "onProviderEnabled: $provider")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            Log.i(TAG, "onStatusChanged: $provider")
        }
    }

    private var mLocationListeners = arrayOf(LocationListener(LocationManager.GPS_PROVIDER), LocationListener(LocationManager.NETWORK_PROVIDER))

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        return Service.START_STICKY
    }

    override fun onCreate() {
        Log.i(TAG, "onCreate")
        initializeLocationManager()
        try {
            mLocationManager!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL.toLong(), LOCATION_DISTANCE,
                    mLocationListeners[1])
        } catch (ex: java.lang.SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(TAG, "network provider does not exist, " + ex.message)
        }

        try {
            mLocationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL.toLong(), LOCATION_DISTANCE,
                    mLocationListeners[0])
        } catch (ex: java.lang.SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(TAG, "gps provider does not exist " + ex.message)
        }

    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy")
        super.onDestroy()
        if (mLocationManager != null) {
            for (i in mLocationListeners.indices) {
                try {
                    mLocationManager!!.removeUpdates(mLocationListeners[i])
                } catch (ex: Exception) {
                    Log.i(TAG, "fail to remove location listeners, ignore", ex)
                }

            }
        }
    }

    private fun initializeLocationManager() {
        Log.i(TAG, "initializeLocationManager")
        if (mLocationManager == null) {

            mLocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }}
