package com.mercadopago.android.px.internal.features.security_code.tracking

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.model.Reason
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel

class SecurityCodeViewTrack(model: TrackingMapModel, reason: Reason): SecurityCodeTrack(model, reason) {
    override fun getTrack() = TrackFactory.withView("$ACTION_BASE_PATH$actionPath").addData(data).build()
}