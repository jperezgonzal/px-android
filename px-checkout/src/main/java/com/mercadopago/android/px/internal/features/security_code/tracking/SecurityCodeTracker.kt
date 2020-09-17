package com.mercadopago.android.px.internal.features.security_code.tracking

import com.mercadopago.android.px.tracking.internal.model.Reason
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel

class SecurityCodeTracker(
    private val securityCodeViewTrack: SecurityCodeViewTrack,
    private val securityCodeEventTrack: SecurityCodeEventTrack,
    private val securityCodeFrictions: SecurityCodeFrictions) {

    fun setTrackData(securityCodeData: TrackingMapModel, reason: Reason) {
        securityCodeViewTrack.setTrackData(securityCodeData, reason)
        securityCodeEventTrack.setTrackData(securityCodeData, reason)
        securityCodeFrictions.setTrackData(securityCodeData)
    }

    fun trackConfirmSecurityCode() {
        securityCodeEventTrack.trackConfirmSecurityCode()
    }

    fun trackSecurityCode() {
        securityCodeViewTrack.track()
    }

    fun trackAbortSecurityCode() {
        securityCodeEventTrack.trackAbortSecurityCode()
    }

    fun trackPaymentApiError() {
        securityCodeFrictions.trackPaymentApiError()
    }

    fun trackTokenApiError() {
        securityCodeFrictions.trackTokenApiError()
    }
}