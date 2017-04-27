package com.ebanx.library

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class GestureListener internal constructor(private val animator: GestureListener.GestureAnimator,
                                           private val menuContainer: View,
                                           private val contentContainer: View,
                                           private val endOffSet: Int,
                                           val screenSize: Int,
                                           private val expandedContentHeight: Int,
                                           private val collapsedContentHeight: Int)
    : GestureDetector.SimpleOnGestureListener() {

    var isExpanded: Boolean = false
    var isScrolling: Boolean = false
    var isOnFling: Boolean = false
    var isAnimating: Boolean = false
    private val rightEdge: Float
    private val leftEdge: Float


    init {
        isExpanded = false

        rightEdge = screenSize * RIGHT_TRIGGER_MULTIPLICATOR
        leftEdge = screenSize * LEFT_TRIGGER_MULTIPLICATOR
    }

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        val diffY = e2.y - e1.y
        val diffX = e2.x - e1.x

        if (!isScrolling && Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > 30) {
            isScrolling = true
        }

        if (!isScrolling) {
            return false
        }

        if (!isExpanded && diffX > 0 && isScrolling && e1.x < leftEdge) {
            contentContainer.x = diffX
            //            menuContainer.setX(diffX - contentContainer.getWidth());

            val layoutParams = contentContainer.layoutParams
            layoutParams.height = (expandedContentHeight - 0.15 * expandedContentHeight.toDouble() * contentContainer.x.toDouble() / screenSize).toInt()
            contentContainer.layoutParams = layoutParams

            return false
        } else if (isExpanded && diffX < 0 && isScrolling && e1.x > rightEdge) {
            contentContainer.x = diffX + contentContainer.width
            //            menuContainer.setX(diffX);

            val layoutParams = contentContainer.layoutParams
            layoutParams.height = (expandedContentHeight - 0.15 * expandedContentHeight.toDouble() * contentContainer.x.toDouble() / screenSize).toInt()
            contentContainer.layoutParams = layoutParams

            return false
        }

        return false
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        if (!isScrolling && (Math.abs(velocityX) < FLING_TRIGGER_VELOCITY
                || Math.abs(velocityX) < Math.abs(velocityY))) {
            return true
        }

        val diffX = e2.x - e1.x

        isOnFling = true

        if (diffX > 40 && e1.x < leftEdge) {
            animator.flingWidthAnimation(contentContainer.x,
                    (screenSize - endOffSet).toFloat(),
                    expandedContentHeight,
                    collapsedContentHeight,
                    contentContainer,
                    menuContainer)

            return true
        }

        if (diffX < -40 && e1.x > rightEdge) {
            animator.flingWidthAnimation(contentContainer.x,
                    0f,
                    collapsedContentHeight,
                    expandedContentHeight,
                    contentContainer,
                    menuContainer)
            return true
        }

        return false
    }

    internal interface GestureAnimator {
        fun flingWidthAnimation(initialX: Float,
                                finalX: Float,
                                initialHeight: Int,
                                finalHeight: Int,
                                slideView: View,
                                expandView: View)
    }

    companion object {
        private val RIGHT_TRIGGER_MULTIPLICATOR = 9f / 10f
        private val LEFT_TRIGGER_MULTIPLICATOR = 1 / 10f
        private val FLING_TRIGGER_VELOCITY = 2000f
    }
}
