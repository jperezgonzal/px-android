package com.mercadopago.android.px.internal.callbacks

import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.services.Callback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun <T> MPCall<T>.awaitCallback(): Response<T, ApiException> =
    suspendCancellableCoroutine { cont ->
        enqueue(object : Callback<T>() {
            override fun success(result: T) {
                cont.resume(Response.Success(result))
            }

            override fun failure(apiException: ApiException?) {
                apiException?.let {
                    cont.resume(Response.Failure(it))
                }
            }
        })
    }

suspend fun <T> MPCall<T>.awaitTaggedCallback(requestOrigin: String): Response<T, MercadoPagoError> =
    suspendCancellableCoroutine { cont ->
        enqueue(object : TaggedCallback<T>(requestOrigin) {
            override fun onSuccess(result: T) {
                cont.resume(Response.Success(result))
            }

            override fun onFailure(error: MercadoPagoError?) {
                error?.let {
                    cont.resume(Response.Failure(it))
                }
            }
        })
    }