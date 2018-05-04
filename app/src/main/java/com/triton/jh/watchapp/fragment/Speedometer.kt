package com.triton.jh.watchapp.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.triton.jh.watchapp.MainActivity
import com.triton.jh.watchapp.gps.GpsTracker
import com.triton.jh.watchapp.R
import com.triton.jh.watchapp.Tachometer
import com.triton.jh.watchapp.common.SectionPage

/**
 * Created by jh on 28.03.2018.
 */
class Speedometer : Fragment(), SectionPage
{
    companion object {
        private const val MAX_SPEED = "max_speed"
        private const val AVG_SPEED = "avg_speed"
    }

    private var mTextView : TextView? = null
    private var mTachometer: Tachometer? =  null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_speedometer, container, false)

        mTextView =  view.findViewById(R.id.speedometer_view)
        mTachometer = view.findViewById(R.id.speed_tachometer)

        return view
    }

    override fun update(startTime: Long, pauseTime: Long)
    {
        val speedCounter = Tachometer.ArcValue()
        val maxTick = Tachometer.ArcValue()
        val avgTick = Tachometer.ArcValue()

        mTextView?.text = String.format("%05.1f", GpsTracker.speed)

        maxTick.color = Tachometer.COLOR_RED
        maxTick.current = GpsTracker.maxSpeed
        maxTick.max = 20F

        avgTick.color = Tachometer.COLOR_YELLOW
        avgTick.current = GpsTracker.avgSpeed
        avgTick.max = 20F

        speedCounter.current = GpsTracker.speed
        speedCounter.max = 20F
        speedCounter.color = Tachometer.COLOR_BLUE
        speedCounter.ticks = arrayOf(maxTick, avgTick)

        mTachometer?.update(speedCounter)
    }

    override fun saveState(preferences: SharedPreferences.Editor)
    {
        preferences.putFloat(MAX_SPEED, GpsTracker.maxSpeed)
        preferences.putFloat(AVG_SPEED, GpsTracker.avgSpeed)
    }

    override  fun restoreState(preferences: SharedPreferences)
    {
        GpsTracker.maxSpeed = preferences.getFloat(MAX_SPEED, 0F)
        GpsTracker.avgSpeed = preferences.getFloat(AVG_SPEED, 0F)
    }

    override fun reset() {
        GpsTracker.maxSpeed = 0F
        GpsTracker.avgSpeed = 0F
    }
}