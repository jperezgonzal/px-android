package com.mercadopago.android.px.internal.extensions

import android.app.Activity
import android.graphics.Rect
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment

internal fun CharSequence?.isNotNullNorEmpty() = !isNullOrEmpty()

internal fun <T : CharSequence> T?.orIfEmpty(fallback: T) = if (isNotNullNorEmpty()) this!! else fallback

internal fun Any?.runIfNull(action: () -> Unit) {
    if (this == null) {
        action.invoke()
    }
}

internal inline fun <reified T> notNull(param: T?) = param ?: error("${T::class.java.simpleName} not be null")

internal inline fun <T : Any?, R> T?.runIfNotNull(action: (T) -> R): R? = this?.run { action(this) }

internal fun <T : CharSequence> T?.runIfNotNullNorEmpty(action: (T) -> Unit): Boolean {
    if (isNotNullNorEmpty()) {
        action.invoke(this!!)
        return true
    }
    return false
}

internal fun Fragment.postDelayed(delay: Long, runnable: (() -> Unit)) = view?.postDelayed(runnable, delay)

internal fun Activity.addKeyBoardListener(
    onKeyBoardOpen: (() -> Unit)? = null,
    onKeyBoardClose: (() -> Unit)? = null
) {
    window.decorView.rootView?.apply {
        viewTreeObserver?.addOnGlobalLayoutListener {
            val r = Rect()

            getWindowVisibleDisplayFrame(r)

            val heightDiff = rootView.height - (r.bottom - r.top)
            if (heightDiff > rootView.height * 0.15) {
                onKeyBoardOpen?.invoke()
            } else {
                onKeyBoardClose?.invoke()
            }
        }
    }
}