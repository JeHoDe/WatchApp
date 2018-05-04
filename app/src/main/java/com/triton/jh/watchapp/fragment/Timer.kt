package com.triton.jh.watchapp.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.triton.jh.watchapp.MainActivity
import com.triton.jh.watchapp.R
import com.triton.jh.watchapp.Tachometer
import com.triton.jh.watchapp.common.SectionPage
import com.triton.jh.watchapp.gps.GpsTracker
import java.util.ArrayList

class Timer : Fragment(), SectionPage
{
    private var mTextView : TextView? = null
    private var mTachometer: Tachometer? =  null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_main, container, false)

        mTextView =  view.findViewById(R.id.timer_view)
        mTachometer = view.findViewById(R.id.timer_tachometer)

        return view
    }

    override fun update(startTime: Long, pauseTime: Long)
    {
        val timerCounter = Tachometer.ArcValue()
        var millisecondTime = 0L

        if (startTime > 0) {
            millisecondTime = SystemClock.uptimeMillis() - startTime + pauseTime
        }

        var seconds = millisecondTime / 1000
        var minutes = seconds / 60
        var hours = minutes / 60

        minutes %= 60
        seconds %= 60

        mTextView?.text =
                String.format("%02d", hours) + ":" +
                String.format("%02d", minutes) + ":" +
                String.format("%02d", seconds)

        timerCounter.current = seconds.toFloat()
        timerCounter.color = Tachometer.COLOR_BLUE
        timerCounter.max = 60F

        val ticks = ArrayList<Tachometer.ArcValue>()

        if (hours > 0F) {

            val hoursTick = Tachometer.ArcValue()

            hoursTick.color = Tachometer.COLOR_YELLOW
            hoursTick.current = hours % 24F
            hoursTick.max = 24F

            ticks.add(hoursTick)
        }

        if (minutes > 0F) {

            val minutesTick = Tachometer.ArcValue()

            minutesTick.current = minutes.toFloat()
            minutesTick.color = Tachometer.COLOR_RED
            minutesTick.max = 60F

            ticks.add(minutesTick)
        }

        if (ticks.size > 0) {
            timerCounter.ticks = ticks.toTypedArray()
        }

        mTachometer?.update(timerCounter)
    }

    override fun saveState(preferences: SharedPreferences.Editor)
    {

    }

    override fun restoreState(preferences: SharedPreferences)
    {

    }

    override fun reset() {

    }
}
