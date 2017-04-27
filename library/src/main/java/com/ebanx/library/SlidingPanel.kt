package com.ebanx.library

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.support.annotation.AttrRes
import android.support.annotation.StyleRes
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout


class SlidingPanel : FrameLayout, GestureListener.GestureAnimator{

    var menuContainer : View? = null
    var contentContainer : View? = null

    var gestureListener : GestureListener? = null
    var gestureDetector : GestureDetectorCompat? = null

    var frontViewExpandedHeight : Int = 0
    var frontViewCollapsedHeight : Int = 0

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context)
    }

    fun init(context: Context){

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        menuContainer = getChildAt(0)
        contentContainer = getChildAt(1)

        gestureListener = GestureListener(this, menuContainer!!, contentContainer!!, 0, w, h, (h * 0.8).toInt())
        gestureDetector = GestureDetectorCompat(context, gestureListener)
    }

    override fun flingWidthAnimation(initialX: Float, finalX: Float, initialHeight : Int,
                                     finalHeight : Int, frontView: View, behindView: View) {
        if(initialX == finalX){
            gestureListener!!.isExpanded = initialX != 0F
            return
        }

        val xAnimator = ValueAnimator.ofFloat(initialX, finalX)
        xAnimator.addUpdateListener {
            val xAnimated = it.animatedValue as Float

            frontView.x = xAnimated
//            behindView.x = xAnimated - behindView.width
        }

        val heightAnimation = ValueAnimator.ofInt(initialHeight, finalHeight)
        heightAnimation.addUpdateListener {
            val heigthAnimated = it.animatedValue as Int

            val layoutParams = frontView.layoutParams
            layoutParams.height = heigthAnimated
            frontView.layoutParams = layoutParams
        }

        val animatorSet = AnimatorSet()
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.duration = 200


        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                gestureListener!!.isOnFling = false
                gestureListener!!.isScrolling = false
                gestureListener!!.isExpanded = finalX >= initialX && initialX != 0F
            }
        })

        animatorSet.playTogether(xAnimator, heightAnimation)
        animatorSet.start()
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (gestureDetector!!.onTouchEvent(event)) {
            return true
        }

        if(frontViewExpandedHeight == 0) {
            frontViewExpandedHeight = contentContainer!!.height
        }

        if(frontViewCollapsedHeight == 0) {
            frontViewCollapsedHeight = (contentContainer!!.height * 0.85).toInt()
        }

        if (event!!.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL &&
                gestureListener!!.isScrolling) {

            if (contentContainer!!.x > gestureListener!!.screenSize / 3) {
                slideAnimation(contentContainer!!.x,
                        gestureListener!!.screenSize - 170F,
                        contentContainer!!.height,
                        frontViewCollapsedHeight,
                        contentContainer!!,
                        menuContainer!!)

                Log.d("Touch", "Animated right")
            } else {
                slideAnimation(contentContainer!!.x,
                        0F,
                        contentContainer!!.height,
                        frontViewExpandedHeight,
                        contentContainer!!,
                        menuContainer!!)

                Log.d("Touch", "Animated left")

            }
        }

        return super.dispatchTouchEvent(event)
    }

    private fun slideAnimation(initialX : Float, finalX : Float, initialHeight : Int,
                               finalHeight : Int, frontView : View, behindView : View) {
        if(initialX == finalX){
            gestureListener!!.isExpanded = initialX != 0F
            return
        }

        val xAnimator = ValueAnimator.ofFloat(initialX, finalX)
        xAnimator.addUpdateListener {
            val xAnimated = it.animatedValue as Float

            frontView.x = xAnimated
//            behindView.x = xAnimated - behindView.width
        }

        val heightAnimation = ValueAnimator.ofInt(initialHeight, finalHeight)
        heightAnimation.addUpdateListener {
            val heigthAnimated = it.animatedValue as Int

            val layoutParams = frontView.layoutParams
            layoutParams.height = heigthAnimated
            frontView.layoutParams = layoutParams
        }

        val animatorSet = AnimatorSet()
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.duration = 200

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                gestureListener!!.isScrolling = false

                gestureListener!!.isExpanded = finalX >= initialX && initialX != 0F
            }
        })

        animatorSet.playTogether(xAnimator, heightAnimation)
        animatorSet.start()
    }
}
