package com.mercadopago.android.px.internal.extensions

import android.app.Activity
import android.graphics.Rect
import android.view.View
import androidx.core.view.ViewCompat

internal fun CharSequence?.isNotNullNorEmpty() = !isNullOrEmpty()

internal fun <T : CharSequence> T?.orIfEmpty(fallback: T) = if (isNotNullNorEmpty()) this!! else fallback

internal fun Any?.runIfNull(action: () -> Unit) {
    if (this == null) {
        action.invoke()
    }
}

internal fun <T : CharSequence> T?.runIfNotNullNorEmpty(action: (T) -> Unit): Boolean {
    if (isNotNullNorEmpty()) {
        action.invoke(this!!)
        return true
    }
    return false
}

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