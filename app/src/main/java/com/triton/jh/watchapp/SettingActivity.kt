package com.triton.jh.watchapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import com.triton.jh.watchapp.common.SectionPage

/**
 * Created by jh on 18.03.2018.
 */
class SettingActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SettingActivity"

        private var pages: Array<SectionPage> = arrayOf(
        )
    }

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private var mSliderBar: LinearLayout? = null

    private var mDots: Array<ImageView?>? = null

    private var mGestureDetector: GestureDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        val pageContainer = findViewById<ViewPager>(R.id.setting_container)

        mSliderBar = findViewById(R.id.setting_slider_bar)
        mDots = Array(mSectionsPagerAdapter!!.count) { null }

        for (i in 0 until mSectionsPagerAdapter!!.count)
        {
            mDots!![i] = ImageView(this)
            mDots!![i]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.default_dot))

            val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(10,10)
            params.setMargins(8, 0, 8, 0)
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

        val layout = findViewById<ViewPager>(R.id.setting_container)// get your root  layout
        layout.setOnTouchListener {_, event ->
            touchEvent(event)
            false
        }

        mGestureDetector =  GestureDetector(this, object : OnSwipeListener() {

            override fun onSwipe(direction: Direction) : Boolean {

                if (direction == Direction.up) {
                    //do your stuff
                    Log.d(SettingActivity.TAG, "onSwipe: up")

                    openMain(findViewById<ViewPager>(R.id.setting_container))
                }

                if (direction == Direction.down) {
                    //do your stuff
                    Log.d(SettingActivity.TAG, "onSwipe: down")
                }
                return true
            }
        })
    }

    private fun touchEvent(event: MotionEvent) {

        mGestureDetector!!.onTouchEvent(event)
    }

    fun openMain(view: View) {

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fadein,R.anim.fadeout)
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            return 2
        }
    }

    fun openSettings(view: View) {
        val intent = Intent(this, SettingActivity::class.java)
        startActivity(intent)
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {

            when (arguments.getInt(ARG_SECTION_NUMBER))
            {
                2 -> return inflater.inflate(R.layout.fragment_settings, container, false)
            }
            return inflater.inflate(R.layout.fragment_history, container, false)
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }
}