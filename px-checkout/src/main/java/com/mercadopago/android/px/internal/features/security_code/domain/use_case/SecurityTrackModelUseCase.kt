package com.mercadopago.android.px.internal.features.security_code.domain.use_case

import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.extensions.notNull
import com.mercadopago.android.px.internal.features.security_code.tracking.SecurityCodeEventTrack
import com.mercadopago.android.px.internal.features.security_code.tracking.SecurityCodeFrictions
import com.mercadopago.android.px.internal.features.security_code.tracking.SecurityCodeTracker
import com.mercadopago.android.px.internal.features.security_code.tracking.SecurityCodeViewTrack
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.tracking.internal.model.Reason
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel

class SecurityTrackModelUseCase(
    private val userSelectionRepository: UserSelectionRepository
) : UseCase<Reason, SecurityCodeTracker>() {

    override suspend fun doExecute(param: Reason) = notNull(userSelectionRepository.card).let { card ->
        val model = SecurityTrackModel(card)
        val securityCodeTracker = SecurityCodeTracker(
            SecurityCodeViewTrack(model, param),
            SecurityCodeEventTrack(model, param),
            SecurityCodeFrictions(model))

        Response.Success(securityCodeTracker)
    }

    inner class SecurityTrackModel(private val card: Card) : TrackingMapModel() {
        override fun toMap() = mapOf(
            "payment_method_id" to card.paymentMethod!!.id,
            "payment_method_type" to card.paymentMethod!!.paymentTypeId,
            "card_id" to card.id,
            "issuer_id" to card.issuer?.id?.toString().orEmpty(),
            "bin" to card.firstSixDigits.orEmpty()
        )
    }
}