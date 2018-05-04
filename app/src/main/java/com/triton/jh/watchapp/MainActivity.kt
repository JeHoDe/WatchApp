package com.triton.jh.watchapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle

import android.os.Handler
import android.os.SystemClock
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.TextView
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import com.triton.jh.watchapp.R.id.*
import com.triton.jh.watchapp.common.SectionPage
import com.triton.jh.watchapp.gps.GpsTracker

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val RUNNING = "running"
        private const val START_TIME = "start_time"

        private var pages: Array<SectionPage> = arrayOf(
            com.triton.jh.watchapp.fragment.Timer(),
            com.triton.jh.watchapp.fragment.Odometer(),
            com.triton.jh.watchapp.fragment.Altimeter(),
            com.triton.jh.watchapp.fragment.Speedometer()
        )
    }

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private var mHandler: Handler? = null

    private var mStartTime = 0L

    private var mPauseTime = 0L

    private var mSliderBar: LinearLayout? = null

    private var mDots: Array<ImageView?>? = null

    private var mRunning: Boolean = false

    private var mGpsService: Intent? = null

    private var mGestureDetector: GestureDetector? = null

    private var mRunnable: Runnable = object : Runnable {

        @SuppressLint("SetTextI18n")
        override fun run() {

            val gpsIndicator = findViewById<ImageView>(R.id.no_gps)

            if (GpsTracker.signal) {
                gpsIndicator.visibility = View.GONE
            } else {
                gpsIndicator.visibility = View.VISIBLE
            }

            if (mRunning) {

                for (page in pages)
                    page.update(mStartTime, mPauseTime)

                mHandler?.postDelayed(this, 1000)
            }
        }
    }

    private var mResetHandler: Runnable = object : Runnable {
        override fun run() {
            reset()
        }
    }

    private fun save() {

        val preferences = getPreferences(Context.MODE_PRIVATE)
        val bag = preferences.edit()

        for (page in pages)
            page.saveState(bag)

        bag.commit()
    }

    private fun restore() {

        val preferences = getPreferences(Context.MODE_PRIVATE)

        mStartTime = preferences.getLong(MainActivity.START_TIME, 0L)
        mRunning = preferences.getBoolean(MainActivity.RUNNING, false)

        for (page in pages)
            page.restoreState(preferences)

        if (mRunning) {
            mHandler?.postDelayed(this.mRunnable, 0)
        }
    }

    override fun onPause() {
        super.onPause()

        Log.d(TAG, "onPause")
        save()
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume")
        restore()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    Array(1) {Manifest.permission.ACCESS_FINE_LOCATION }, 123)
        }

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        val pageContainer = findViewById<ViewPager>(R.id.page_container)

        mSliderBar = findViewById(R.id.slider_bar)
        mDots = Array(mSectionsPagerAdapter!!.count) { null }

        for (i in 0 until mSectionsPagerAdapter!!.count)
        {
            mDots!![i] = ImageView(this)
            mDots!![i]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.default_dot))

            val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(10,10)
            params.setMargins(2, 0, 2, 0)
            mSliderBar?.addView(mDots!![i], params)
        }

        mDots!![0]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.selected_dot))

        // Set up the ViewPager with the sections adapter.
        pageContainer.adapter = mSectionsPagerAdapter
        pageContainer.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                for (i in 0 until mSectionsPagerAdapter!!.count) {
                    mDots!![i]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.default_dot))
                }

                mDots!![position]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.selected_dot))
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        val layout = findViewById<View>(R.id.page_container)// get your root  layout
        layout.setOnTouchListener {_, event ->
            touchEvent(event)
            false
        }

        mGestureDetector =  GestureDetector(this, object : OnSwipeListener() {

            override fun onSwipe(direction: OnSwipeListener.Direction) : Boolean {

                if (direction == OnSwipeListener.Direction.up) {
                    //do your stuff
                    Log.d(TAG, "onSwipe: up")

                    //openMain(findViewById<ViewPager>(R.id.setting_container))
                }

                if (direction == OnSwipeListener.Direction.down) {
                    //do your stuff
                    Log.d(TAG, "onSwipe: down")

                    //openSettings(findViewById<ViewPager>(R.id.page_container))
                }
                return true
            }
        })

        mHandler = Handler()
        mGpsService = Intent(this, GpsTracker::class.java)
    }

    private fun touchEvent(event: MotionEvent) {

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mHandler!!.postDelayed(mResetHandler, 3000L)
            }
            MotionEvent.ACTION_UP -> {
                mHandler!!.removeCallbacks(mResetHandler)

                if (event.eventTime - event.downTime in 401..2999) {
                    startPause()
                }
            }
        }

        mGestureDetector!!.onTouchEvent(event)
    }

    private fun startPause() {

        if (mStartTime == 0L) {

            mStartTime = SystemClock.uptimeMillis()
        }

        val pauseIndicator = findViewById<ImageView>(R.id.pause)

        if (!mRunning) {

            if (mPauseTime > 0) {
                mStartTime += (SystemClock.uptimeMillis() - mPauseTime)
                mPauseTime = 0
            }

            mRunning = true
            mHandler?.postDelayed(this.mRunnable, 0)

            pauseIndicator.visibility = View.GONE
            startService(mGpsService)

        } else {

            mPauseTime = SystemClock.uptimeMillis()
            mRunning = false

            pauseIndicator.visibility = View.VISIBLE
            stopService(mGpsService)
        }
    }

    private fun reset() {

        mRunning = false

        mStartTime = 0
        mPauseTime = 0

        for (page in pages)
            page.reset()

        stopService(mGpsService)

        val gpsIndicator = findViewById<ImageView>(R.id.no_gps)
        val pauseIndicator = findViewById<ImageView>(R.id.pause)

        gpsIndicator.visibility = View.GONE
        pauseIndicator.visibility = View.GONE

        for (page in pages)
            page.update(mStartTime, mPauseTime)

        GpsTracker.reset()
        save()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun openSettings(view: View) {

        view.visibility = View.GONE

        var settingViewPager = findViewById<ViewPager>(R.id.setting_container)
        settingViewPager.visibility = View.VISIBLE
        settingViewPager.setOnTouchListener {_, event ->
            touchEvent(event)
            false
        }

        return

        val intent = Intent(this, SettingActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fadein,R.anim.fadeout)
    }

    fun openMain(view: View) {

        view.visibility = View.GONE

        var mainViewPager = findViewById<ViewPager>(R.id.page_container);
        mainViewPager.visibility = View.VISIBLE
        mainViewPager.setOnTouchListener {_, event ->
            touchEvent(event)
            false
        }
    }

        /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            // return PlaceholderFragment.newInstance(position + 1)

            return pages[position] as Fragment
        }

        override fun getCount(): Int {
            return pages.count()
        }
    }
}
