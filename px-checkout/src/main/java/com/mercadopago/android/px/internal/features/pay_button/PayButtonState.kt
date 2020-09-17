package com.mercadopago.android.px.internal.features.pay_button

import com.mercadopago.android.px.R
import com.mercadopago.android.px.addons.model.SecurityValidationData
import com.mercadopago.android.px.internal.features.explode.ExplodeDecorator
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.internal.viewmodel.PayButtonViewModel as ButtonConfig

internal sealed class PayButtonState

internal open class UIProgress : PayButtonState() {
    data class FingerprintRequired(val validationData: SecurityValidationData) : UIProgress()
    data class ButtonLoadingStarted(val timeOut: Int, val buttonConfig: ButtonConfig) : UIProgress()
    data class ButtonLoadingFinished(val explodeDecorator: ExplodeDecorator) : UIProgress()
    object ButtonLoadingCanceled : UIProgress()
}

internal open class UIResult : PayButtonState() {
    object VisualProcessorResult : UIResult()
    data class PaymentResult(val model: PaymentModel) : UIResult()
    data class BusinessPaymentResult(val model: BusinessPaymentModel) : UIResult()
    data class NoCongratsResult(val model: PaymentModel) : UIResult()
}

internal open class UIError : PayButtonState() {
    class ConnectionError(private val retriesCount: Int): UIError() {
        private val maxRetries = 3
        private val neutralMessage = R.string.px_connectivity_neutral_error
        private val errorMessage = R.string.px_connectivity_error

        val message: Int
            get() = if (retriesCount <= maxRetries) neutralMessage else errorMessage
        val actionMessage: Int?
            get() = if (retriesCount > maxRetries) R.string.px_snackbar_error_action else null

    }
    object BusinessError : UIError()
}