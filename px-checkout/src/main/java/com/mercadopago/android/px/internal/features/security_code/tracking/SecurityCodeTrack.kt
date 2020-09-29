package com.mercadopago.android.px.internal.features.security_code.tracking

import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.Reason
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel

abstract class SecurityCodeTrack(securityCodeData: TrackingMapModel, reason: Reason): TrackWrapper() {

    protected val data = mutableMapOf<String, Any>("reason" to reason.name).also { it.putAll(securityCodeData.toMap()) }
    protected var actionPath: String = ""

    companion object {
        const val ACTION_BASE_PATH = "${BASE_PATH}/security_code"
    }
}