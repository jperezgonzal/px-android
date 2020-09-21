package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.internal.datasource.cache.Cache
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository
import com.mercadopago.android.px.internal.repository.ExperimentsRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.services.CheckoutService
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.callbacks.awaitCallback
import com.mercadopago.android.px.internal.tracking.TrackingRepository
import com.mercadopago.android.px.model.internal.InitResponse

class CheckoutRepositoryImpl(
    paymentSettingRepository: PaymentSettingRepository,
    experimentsRepository: ExperimentsRepository,
    disabledPaymentMethodRepository: DisabledPaymentMethodRepository,
    escManagerBehaviour: ESCManagerBehaviour,
    checkoutService: CheckoutService,
    trackingRepository: TrackingRepository,
    initCache: Cache<InitResponse>) : InitService(paymentSettingRepository, experimentsRepository,
        disabledPaymentMethodRepository, escManagerBehaviour, checkoutService, trackingRepository, initCache) {

    override suspend fun loadInitResponse(): InitResponse? =
        when (val callbackResult = init().awaitCallback()) {
            is Response.Success -> callbackResult.result
            is Response.Failure -> null
        }
}