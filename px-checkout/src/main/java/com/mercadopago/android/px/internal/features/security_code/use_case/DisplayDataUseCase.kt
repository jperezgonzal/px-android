package com.mercadopago.android.px.internal.features.security_code.use_case

import com.mercadopago.android.px.internal.base.response.Response
import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.extensions.notNull
import com.mercadopago.android.px.internal.repository.InitRepository
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.model.CardDisplayInfo
import com.mercadopago.android.px.model.CvvInfo
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel

class DisplayDataUseCase(
    private val initRepository: InitRepository,
    private val userSelectionRepository: UserSelectionRepository
) : UseCase<Unit, DisplayDataUseCase.SecurityCodeDisplayData>() {

    override suspend fun buildUseCase(param: Unit) = notNull(userSelectionRepository.card).let { card ->
        val trackModel = buildTrackModel(card)
        val securityCodeLength = card.getSecurityCodeLength() ?: 0
        card.paymentMethod?.displayInfo?.cvvInfo?.let {
            Response.Success(SecurityCodeDisplayData(null, it, securityCodeLength, trackModel))
        } ?: notNull(initRepository.loadInitResponse()).let { initResponse ->
            val expressMetadata = notNull(initResponse.express.find { data -> data.isCard && data.card.id == card.id })
            val cardDisplayInfo = notNull(expressMetadata.card?.displayInfo)

            Response.Success(SecurityCodeDisplayData(cardDisplayInfo, null, securityCodeLength, trackModel))
        }
    }

    private fun buildTrackModel(card: Card) = object : TrackingMapModel() {
        override fun toMap() = mapOf(
            "payment_method_id" to card.paymentMethod!!.id,
            "payment_method_type" to card.paymentMethod!!.paymentTypeId,
            "card_id" to card.id,
            "issuer_id" to card.issuer?.id?.toString().orEmpty(),
            "bin" to card.firstSixDigits.orEmpty()
        )
    }

    inner class SecurityCodeDisplayData(
        val cardDisplayInfo: CardDisplayInfo?,
        val cvvInfo: CvvInfo?,
        val securityCodeLength: Int,
        val trackingModel: TrackingMapModel)
}