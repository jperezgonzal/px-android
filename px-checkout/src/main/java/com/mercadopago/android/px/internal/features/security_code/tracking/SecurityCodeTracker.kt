package com.mercadopago.android.px.internal.features.security_code.tracking

class SecurityCodeTracker(
    private val securityCodeViewTrack: SecurityCodeViewTrack,
    private val securityCodeEventTrack: SecurityCodeEventTrack,
    private val securityCodeFrictions: SecurityCodeFrictions) {

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