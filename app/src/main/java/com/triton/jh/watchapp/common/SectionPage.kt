package com.triton.jh.watchapp.common

import android.content.SharedPreferences

/**
 * Created by jh on 28.03.2018.
 */
interface SectionPage {
    fun update(startTime: Long, pauseTime: Long)
    fun saveState(preferences: SharedPreferences.Editor)
    fun restoreState(preferences: SharedPreferences)
    fun reset()
}