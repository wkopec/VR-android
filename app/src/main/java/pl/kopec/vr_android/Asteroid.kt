package pl.kopec.vr_android

import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.animation.Animation
import jmini3d.Object3d
import android.animation.Animator

class Asteroid(val object3d: Object3d, private val onAsteroidHitListener: OnAsteroidHitListener) {

    private lateinit var xAnimator: ValueAnimator
    private lateinit var yAnimator: ValueAnimator
    private lateinit var zAnimator: ValueAnimator

    var isAsteroidDestroyed = false

    fun updatePosition() {
        this.object3d.setPosition(
            xAnimator.animatedValue as Float,
            yAnimator.animatedValue as Float,
            zAnimator.animatedValue as Float
        )
    }

    fun initAsteroid() {
        this.object3d.scale = 0.02f
        setupPositionAnimator()
    }

    private fun setupPositionAnimator() {
        xAnimator = ValueAnimator.ofFloat(2f, 0f)
        yAnimator = ValueAnimator.ofFloat(3f, 0f)
        zAnimator = ValueAnimator.ofFloat(-50f, 1f)

        xAnimator.repeatCount = Animation.ABSOLUTE
        xAnimator.duration = 5000
        yAnimator.repeatCount = Animation.ABSOLUTE
        yAnimator.duration = 5000
        zAnimator.repeatCount = Animation.ABSOLUTE
        zAnimator.duration = 5000


        xAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onHit()
            }
        })

        xAnimator.start()
        yAnimator.start()
        zAnimator.start()
    }

    private fun onHit() {
        if(!isAsteroidDestroyed) {
            onAsteroidHitListener.onAsteroidHit(this)
        }
    }

}