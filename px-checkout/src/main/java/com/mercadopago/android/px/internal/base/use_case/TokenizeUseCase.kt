package com.mercadopago.android.px.internal.base.use_case

import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.internal.extensions.notNull
import com.mercadopago.android.px.internal.extensions.runIfNotNull
import com.mercadopago.android.px.internal.repository.CardTokenRepository
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.internal.util.CVVRecoveryWrapper
import com.mercadopago.android.px.internal.util.TokenCreationWrapper
import com.mercadopago.android.px.model.PaymentRecovery
import com.mercadopago.android.px.model.Token

class TokenizeUseCase(
    private val cardTokenRepository: CardTokenRepository,
    private val escManagerBehaviour: ESCManagerBehaviour,
    private val userSelectionRepository: UserSelectionRepository
) : UseCase<TokenizeParams, Token>() {

    override suspend fun doExecute(param: TokenizeParams) = param.paymentRecovery.runIfNotNull {
        CVVRecoveryWrapper(cardTokenRepository, escManagerBehaviour, it).recoverWithCVV(param.securityCode)
    } ?: notNull(userSelectionRepository.card).let {
        TokenCreationWrapper
            .Builder(cardTokenRepository, escManagerBehaviour)
            .with(it)
            .with(it.paymentMethod!!)
            .build()
            .createToken(param.securityCode)
    }
}

data class TokenizeParams(val securityCode: String, val paymentRecovery: PaymentRecovery? = null)