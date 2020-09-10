package com.mercadopago.android.px.internal.callbacks

import androidx.lifecycle.MutableLiveData
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.model.PaymentRecovery
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.model.Reason

internal class PaymentServiceEventHandler {
    val paymentFinishedLiveData = MutableLiveData<Event<PaymentModel>>()
    val requireCvvLiveData = MutableLiveData<Event<Reason>>()
    val recoverInvalidEscLiveData = MutableLiveData<Event<PaymentRecovery>>()
    val paymentErrorLiveData = MutableLiveData<Event<MercadoPagoError>>()
    val visualPaymentLiveData = MutableLiveData<Event<Unit>>()
}