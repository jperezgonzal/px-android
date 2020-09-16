package com.mercadopago.android.px.internal.features.security_code.tracking

import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel

class SecurityCodeFrictions {
    private val data = mutableMapOf<String, Any>()

    fun setTrackData(data: TrackingMapModel) {
        this.data.putAll(data.toMap())
    }

    fun trackNoConnectionError() {
        val frictionId = FrictionEventTracker.Id.NO_CONNECTION
        FrictionEventTracker.with(
            "${SecurityCodeTrack.ACTION_BASE_PATH}/${frictionId.name}",
            frictionId,
            FrictionEventTracker.Style.SNACKBAR,
            data).track()
    }

    fun trackPaymentApiError() {
        val frictionId = FrictionEventTracker.Id.PAYMENTS_API_ERROR

        FrictionEventTracker.with(
            "${SecurityCodeTrack.ACTION_BASE_PATH}/${frictionId.name}",
            frictionId,
            FrictionEventTracker.Style.SNACKBAR,
            data).track()
    }

    fun trackTokenApiError() {
        val frictionId = FrictionEventTracker.Id.TOKEN_API_ERROR

        FrictionEventTracker.with(
            "${SecurityCodeTrack.ACTION_BASE_PATH}/${frictionId.name}",
            frictionId,
            FrictionEventTracker.Style.SNACKBAR,
            data).track()

    }
}