package com.mercadopago.android.px.internal.features.security_code.use_case

import com.mercadopago.android.px.internal.base.response.Response
import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.extensions.notNull
import com.mercadopago.android.px.internal.repository.InitRepository
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.model.CardDisplayInfo
import com.mercadopago.android.px.model.CvvInfo

class DisplayInfoUseCase(
    private val initRepository: InitRepository,
    private val userSelectionRepository: UserSelectionRepository
) : UseCase<Unit, Triple<CardDisplayInfo?, CvvInfo?, Int>>() {

    override suspend fun doExecute(param: Unit) = notNull(userSelectionRepository.card).let { card ->

        val securityCodeLength = card.getSecurityCodeLength() ?: 0
        card.paymentMethod?.displayInfo?.cvvInfo?.let {
            Response.Success(Triple(null, it, securityCodeLength))
        } ?: notNull(initRepository.loadInitResponse()).let { initResponse ->
            val expressMetadata = notNull(initResponse.express.find { data -> data.isCard && data.card.id == card.id })
            val cardDisplayInfo = notNull(expressMetadata.card?.displayInfo)

            Response.Success(Triple(cardDisplayInfo, null, securityCodeLength))
        }
    }
}