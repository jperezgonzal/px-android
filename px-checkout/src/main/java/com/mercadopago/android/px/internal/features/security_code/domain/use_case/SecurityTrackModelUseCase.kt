package com.mercadopago.android.px.internal.features.security_code.domain.use_case

import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.extensions.notNull
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel

class SecurityTrackModelUseCase(
    private val userSelectionRepository: UserSelectionRepository
) : UseCase<Unit, TrackingMapModel>() {

    override suspend fun doExecute(param: Unit) = notNull(userSelectionRepository.card).let { card ->
        Response.Success(SecurityTrackModel(card))
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