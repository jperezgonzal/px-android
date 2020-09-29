package com.mercadopago.android.px.internal.extensions

import android.os.Build
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.mercadolibre.android.picassodiskcache.PicassoDiskLoader
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Transformation

@JvmOverloads
internal fun ImageView?.loadOrElse(url: String?, @DrawableRes fallback: Int, transformation: Transformation? = null) {
    this?.let {
        it.context?.applicationContext?.let { context ->
            val picasso = PicassoDiskLoader.get(context)
            val requestCreator: RequestCreator = if (url.isNotNullNorEmpty()) picasso.load(url) else picasso.load(fallback)
            transformation?.let { requestCreator.transform(transformation) }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                requestCreator.placeholder(AppCompatResources.getDrawable(context, fallback))
            } else {
                requestCreator.placeholder(fallback)
            }.into(it)
        }
    }
}