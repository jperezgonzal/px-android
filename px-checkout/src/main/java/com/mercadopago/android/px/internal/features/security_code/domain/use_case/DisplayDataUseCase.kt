package com.mercadopago.android.px.internal.features.security_code.domain.use_case

import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.callbacks.map
import com.mercadopago.android.px.internal.extensions.notNull
import com.mercadopago.android.px.internal.features.security_code.data.SecurityCodeDisplayData
import com.mercadopago.android.px.internal.features.security_code.domain.model.BusinessSecurityCodeDisplayData
import com.mercadopago.android.px.internal.features.security_code.mapper.BusinessSecurityCodeDisplayDataMapper
import com.mercadopago.android.px.internal.repository.InitRepository
import com.mercadopago.android.px.internal.repository.UserSelectionRepository

internal class DisplayDataUseCase(
    private val initRepository: InitRepository,
    private val userSelectionRepository: UserSelectionRepository,
    private val securityCodeDisplayDataMapper: BusinessSecurityCodeDisplayDataMapper
) : UseCase<Unit, BusinessSecurityCodeDisplayData>() {

    override suspend fun doExecute(param: Unit) = notNull(userSelectionRepository.card).let { card ->
        val securityCodeLength = card.getSecurityCodeLength() ?: 0
        card.paymentMethod?.displayInfo?.cvvInfo?.let {
            Response.Success(SecurityCodeDisplayData(null, it, securityCodeLength))
        } ?: notNull(initRepository.loadInitResponse()).let { initResponse ->
            val expressMetadata = notNull(initResponse.express.find { data -> data.isCard && data.card.id == card.id })
            val cardDisplayInfo = notNull(expressMetadata.card?.displayInfo)

            Response.Success(SecurityCodeDisplayData(cardDisplayInfo, null, securityCodeLength))
        }
    }.map { securityCodeDisplayDataMapper.map(it) }
}