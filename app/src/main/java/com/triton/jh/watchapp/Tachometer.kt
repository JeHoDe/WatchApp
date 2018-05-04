package com.triton.jh.watchapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.ImageView


/**
 * Created by jh on 15.03.2018.
 */
class Tachometer : ImageView {

    class ArcValue {
        var current:Float = 0F
        var max:Float = 0F
        var color: Int = 0
        var ticks: Array<ArcValue>? = null
    }

    companion object {
        var COLOR_BACKGROUND = Color.parseColor("#404040")
        var COLOR_RED = Color.parseColor("#F02020")
        var COLOR_GREEN = Color.parseColor("#20F020")
        var COLOR_BLUE = Color.parseColor("#2020F0")
        var COLOR_YELLOW = Color.parseColor("#F0F020")
    }


    private val STROKE_WIDTH = 20F

    private var mPaint: Paint? = null

    private var mInner:ArcValue? = null
    private var mOuter:ArcValue? = null

    init {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attributes: AttributeSet) : super(context, attributes)
    constructor(context: Context, attributes: AttributeSet, defStyleAttr: Int) : super(context, attributes, defStyleAttr)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val x = measuredWidth
        val y = measuredHeight
        val diameter = Math.min(x, y)

        mPaint!!.style = Paint.Style.STROKE
        //mPaint!!.pathEffect = CornerPathEffect(10F)
        mPaint!!.strokeWidth = STROKE_WIDTH

        mPaint!!.color = COLOR_BACKGROUND
        canvas.drawCircle(x / 2F, y / 2F, (diameter / 2F) - STROKE_WIDTH / 2, mPaint)


        val rect = RectF(
                x / 2F - (diameter / 2F) + (STROKE_WIDTH / 2),
                y / 2F - (diameter / 2F) + (STROKE_WIDTH / 2),
                x / 2F + (diameter / 2F) - (STROKE_WIDTH / 2),
                y / 2F + (diameter / 2F) - (STROKE_WIDTH / 2))

        if (mOuter != null) {
            mPaint!!.color = mOuter!!.color
            canvas.drawArc(rect, 90F, 360F * (mOuter!!.current / mOuter!!.max), false, mPaint)
        }

        mPaint!!.strokeWidth = STROKE_WIDTH / 2

        if (mInner != null) {
            mPaint!!.color = mInner!!.color
            canvas.drawArc(rect, 90F, 360F * (mInner!!.current / mInner!!.max), false, mPaint)
        }

        mPaint!!.strokeWidth = STROKE_WIDTH

        if (mOuter != null && mOuter!!.ticks != null) {

            for (tick in mOuter!!.ticks!!) {

                mPaint!!.color = tick.color
                canvas.drawArc(rect, 90F + (360F * (tick.current / tick.max)), 1F, false, mPaint)
            }
        }
    }

    /*
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // getHeight() is not reliable, use getMeasuredHeight() on first run:
        // Note: mRect will also be null after a configuration change,
        // so in this case the new measured height and width values will be used:
        if (mRect == null) {
            // take the minimum of width and height here to be on he safe side:
            centerX = measuredWidth / 2
            centerY = measuredHeight / 2
            radius = Math.min(centerX, centerY)

            // mRect will define the drawing space for drawArc()
            // We have to take into account the STROKE_WIDTH with drawArc() as well as drawCircle():
            // circles as well as arcs are drawn 50% outside of the bounds defined by the radius (radius for arcs is calculated from the rectangle mRect).
            // So if mRect is too large, the lines will not fit into the View
            val startTop = STROKE_WIDTH / 2
            val endBottom = 2 * radius - startTop

            val diff = measuredWidth - measuredHeight

            if (diff < 0) {
                startTop -= diff / 2
                mRect = RectF((startTop - diff / 2 ).toFloat(), startTop.toFloat(), endBottom.toFloat(), endBottom.toFloat())

            } else {

            }
        }

        // just to show the rectangle bounds:
        canvas.drawRect(mRect!!, mRectPaint!!)

        // subtract half the stroke width from radius so the blue circle fits inside the View
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), (radius - STROKE_WIDTH / 2).toFloat(), mBasePaint!!)
        // Or draw arc from degree 192 to degree 90 like this ( 258 = (360 - 192) + 90:
        // canvas.drawArc(mRect, 192, 258, false, mBasePaint);

        // draw an arc from 90 degrees to 192 degrees (102 = 192 - 90)
        // Note that these degrees are not like mathematical degrees:
        // they are mirrored along the y-axis and so incremented clockwise (zero degrees is always on the right hand side of the x-axis)
        canvas.drawArc(mRect!!, 90f, 102f, false, mDegreesPaint!!)

        // subtract stroke width from radius so the white circle does not cover the blue circle/ arc
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), (radius - STROKE_WIDTH).toFloat(), mCenterPaint!!)
    }
    */

    fun update(outerCircle:ArcValue, innerCircle:ArcValue? = null) {

        mInner = innerCircle
        mOuter = outerCircle

        invalidate()
    }
}