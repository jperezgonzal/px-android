package com.mercadopago.android.px.internal.base.use_case

import com.mercadopago.android.px.internal.base.response.Response
import com.mercadopago.android.px.internal.extensions.orIfEmpty
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias CallBack<T> = (T) -> Unit

abstract class UseCase<in P, out R> {

    abstract suspend fun buildUseCase(param: P): Response<R, MercadoPagoError>

    fun execute(param: P, success: CallBack<R> = {}, failure: CallBack<MercadoPagoError> = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                buildUseCase(param).also { response ->
                    withContext(Dispatchers.Main) { response.resolve(success, failure) }
                }
            } catch (e: Exception) {
                failure(MercadoPagoError(
                    e.localizedMessage.orIfEmpty("Error when build ${this@UseCase.javaClass}"),
                    false))
            }
        }
    }
}