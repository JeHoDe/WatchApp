package com.triton.jh.watchapp.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.triton.jh.watchapp.gps.GpsTracker
import com.triton.jh.watchapp.R
import com.triton.jh.watchapp.Tachometer
import com.triton.jh.watchapp.common.SectionPage

/**
 * Created by jh on 28.03.2018.
 */
class Odometer : Fragment(), SectionPage
{
    companion object {
        private const val ODO_DIST_METERS = "odo_distance_meters"
    }

    private var mTextView : TextView? = null
    private var mTachometer: Tachometer? =  null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_odometer, container, false)

        mTextView =  view.findViewById(R.id.odometer_view)
        mTachometer = view.findViewById(R.id.odometer_tachometer)

        return view
    }

    override fun update(startTime: Long, pauseTime: Long)
    {
        mTextView?.text = String.format("%05.1f", GpsTracker.distanceInMetres / 1000F)

        val odometerCounter = Tachometer.ArcValue()

        odometerCounter.current = GpsTracker.distanceInMetres.toFloat()
        odometerCounter.max = 20000F
        odometerCounter.color = Tachometer.COLOR_BLUE

        mTachometer?.update(odometerCounter)
    }

    override fun saveState(preferences: SharedPreferences.Editor)
    {
        preferences.putInt(ODO_DIST_METERS, GpsTracker.distanceInMetres)
    }

    override  fun restoreState(preferences: SharedPreferences)
    {
        GpsTracker.distanceInMetres = preferences.getInt(ODO_DIST_METERS, 0)
    }

    override fun reset()
    {
        GpsTracker.distanceInMetres = 0
    }
}