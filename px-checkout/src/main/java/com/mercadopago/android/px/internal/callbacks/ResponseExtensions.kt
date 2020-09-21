package com.mercadopago.android.px.internal.callbacks

import com.mercadopago.android.px.internal.extensions.orIfEmpty
import com.mercadopago.android.px.model.exceptions.MercadoPagoError

internal fun <V, R> Response<V, MercadoPagoError>.map(transform: (V) -> R): Response<R, MercadoPagoError> {
    return when (this) {
        is Response.Success -> {
            try {
                success(transform(result))
            } catch (e: Exception) {
                failure(MercadoPagoError(
                    e.localizedMessage.orIfEmpty("transform operation is not supported"),
                    false))
            }
        }
        is Response.Failure -> failure(exception)
    }
}