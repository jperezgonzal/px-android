package com.mercadopago.android.px.internal.view.animator

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

object AnimatorFactory {

    fun fadeIn(view: View, duration: Long? = null): Animator {
        return alpha(view, 1f).apply {
            duration?.let { this.duration = it }
        }
    }

    fun fadeOut(view: View, duration: Long? = null): Animator {
        return alpha(view, 0f).apply {
            duration?.let { this.duration = it }
        }
    }

    fun scaleAndTranslateY(view: View, scaleFactor: Float, to: Float, from: Float? = null, duration: Long? = null): AnimatorSet {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", scaleFactor)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", scaleFactor)
        val translateY = translateY(view, to, from)
        return AnimatorSet().apply {
            play(scaleX).with(scaleY).with(translateY)
            duration?.let { this.duration = it }
        }
    }

    fun translateY(view: View, to: Float, from: Float? = null, duration: Long? = null): Animator {
        return ObjectAnimator.ofFloat(view, "translationY", to).apply {
            from?.let { setFloatValues(to, it) }
            duration?.let { this.duration = it }
        }
    }

    fun fadeInAndTranslateY(view: View, to: Float, from: Float? = null, duration: Long? = null): AnimatorSet {
        val translateY = translateY(view, to, from)
        val fadeIn = fadeIn(view)
        return AnimatorSet().apply {
            play(translateY).with(fadeIn)
            duration?.let { this.duration = it }
        }
    }

    fun fadeOutAndTranslateY(view: View, to: Float, from: Float? = null, duration: Long? = null): AnimatorSet {
        val translateY = translateY(view, to, from)
        val fadeOut = fadeOut(view)
        return AnimatorSet().apply {
            play(translateY).with(fadeOut)
            duration?.let { this.duration = it }
        }
    }

    private fun alpha(view: View, value: Float) = ObjectAnimator.ofFloat(view, "alpha", value)
}