
/**
 * Created by jh on 28.03.2018.
 */
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
class Altimeter : Fragment(), SectionPage
{
    companion object {
        private const val MAX_ALT = "max_alt"
        private const val MIN_ALT = "min_alt"
        private const val ALT_UP = "alt_up"
        private const val ALT_DOWN = "alt_down"
        private const val ALT_LAST = "alt_last"
    }

    private var mTextView : TextView? = null
    private var mTachometer: Tachometer? =  null

    var mMinAltitude = 0
    var mMaxAltitude = 0

    var mAltUp = 0
    var mAltDown = 0

    var mLastAltitude = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_altimeter, container, false)

        mTextView =  view.findViewById(R.id.altimeter_view)
        mTachometer = view.findViewById(R.id.altimeter_tachometer)

        return view
    }

    override fun update(startTime: Long, pauseTime: Long)
    {
        val altitude = GpsTracker.altitude

        if (mLastAltitude != 0) {

            if (mLastAltitude > altitude) {
                mAltDown += mLastAltitude - altitude
            }
            else if (altitude > mLastAltitude) {
                mAltUp += altitude - mLastAltitude
            }
        }

        if (altitude > mMaxAltitude) {
            mMaxAltitude = altitude
        }

        if (altitude < mMinAltitude || mMinAltitude == 0) {
            mMinAltitude = altitude
        }

        mLastAltitude = altitude
        mTextView?.text = String.format("%03d", altitude)

        val altitudeCounter = Tachometer.ArcValue()
        val minTick = Tachometer.ArcValue()
        val maxTick = Tachometer.ArcValue()

        minTick.color = Tachometer.COLOR_GREEN
        minTick.current = mMinAltitude.toFloat()
        minTick.max = 3000F

        maxTick.color = Tachometer.COLOR_RED
        maxTick.current = mMaxAltitude.toFloat()
        maxTick.max = 3000F

        altitudeCounter.current = GpsTracker.altitude.toFloat()
        altitudeCounter.max = 3000F
        altitudeCounter.color = Tachometer.COLOR_BLUE
        altitudeCounter.ticks = arrayOf(minTick, maxTick)

        mTachometer?.update(altitudeCounter)
    }

    override fun saveState(preferences: SharedPreferences.Editor)
    {
        preferences.putInt(MAX_ALT, mMaxAltitude)
        preferences.putInt(MIN_ALT, mMinAltitude)
        preferences.putInt(ALT_UP, mAltUp)
        preferences.putInt(ALT_DOWN, mAltDown)
        preferences.putInt(ALT_LAST, mLastAltitude)
    }

    override  fun restoreState(preferences: SharedPreferences)
    {
        mMaxAltitude = preferences.getInt(MAX_ALT, 0)
        mMinAltitude = preferences.getInt(MIN_ALT, 0)
        mAltUp = preferences.getInt(ALT_UP, 0)
        mAltDown = preferences.getInt(ALT_DOWN, 0)
        mLastAltitude = preferences.getInt(ALT_LAST, 0)
    }

    override fun reset()
    {
        mMaxAltitude = 0
        mMinAltitude = 0
        mAltUp = 0
        mAltDown = 0
        mLastAltitude = 0
    }
}