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

        val duration = (5000..10000).shuffled().first()
        val x = (-50..50).shuffled().first()
        val y = (0..30).shuffled().first()
        val z = (-50..50).filter { it < -15 || it > 15 }.shuffled().first()

        xAnimator = ValueAnimator.ofFloat(x.toFloat(), 0f)
        yAnimator = ValueAnimator.ofFloat(y.toFloat(), 0f)
        zAnimator = ValueAnimator.ofFloat(z.toFloat(), 0f)

        xAnimator.repeatCount = Animation.ABSOLUTE
        xAnimator.duration = duration.toLong()
        yAnimator.repeatCount = Animation.ABSOLUTE
        yAnimator.duration = duration.toLong()
        zAnimator.repeatCount = Animation.ABSOLUTE
        zAnimator.duration = duration.toLong()


        zAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onHit()
            }
        })

        xAnimator.start()
        yAnimator.start()
        zAnimator.start()
    }

    private fun onHit() {
        if (!isAsteroidDestroyed) {
            onAsteroidHitListener.onAsteroidHit(this)
        }
    }

}