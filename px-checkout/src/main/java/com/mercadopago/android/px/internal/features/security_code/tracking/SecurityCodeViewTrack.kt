package com.mercadopago.android.px.internal.features.security_code.tracking

import com.mercadopago.android.px.tracking.internal.TrackFactory

class SecurityCodeViewTrack: SecurityCodeTrack() {

    fun trackSecurityCode() {
        track()
    }

    override fun getTrack() = TrackFactory.withView("$ACTION_BASE_PATH$actionPath").addData(data).build()
}