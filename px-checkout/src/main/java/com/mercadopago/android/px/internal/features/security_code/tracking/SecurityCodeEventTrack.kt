package com.mercadopago.android.px.internal.features.security_code.tracking

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.model.Reason
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel

class SecurityCodeEventTrack(model: TrackingMapModel, reason: Reason): SecurityCodeTrack(model, reason) {

    fun trackConfirmSecurityCode() {
        actionPath = "/confirm"
        track()
    }

    fun trackAbortSecurityCode() {
        actionPath = "/abort"
        track()
    }

    override fun getTrack() = TrackFactory.withEvent("$ACTION_BASE_PATH$actionPath").addData(data).build()
}