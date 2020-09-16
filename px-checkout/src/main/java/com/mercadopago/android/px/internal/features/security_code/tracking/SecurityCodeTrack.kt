package com.mercadopago.android.px.internal.features.security_code.tracking

import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.Reason
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel

abstract class SecurityCodeTrack: TrackWrapper() {

    protected val data = mutableMapOf<String, Any>()
    protected var actionPath: String = ""

    fun setTrackData(securityCodeData: TrackingMapModel, reason: Reason) {
        data.putAll(securityCodeData.toMap())
        data["reason"] = reason.name
    }

    companion object {
        const val ACTION_BASE_PATH = "${BASE_PATH}/security_code"
    }
}