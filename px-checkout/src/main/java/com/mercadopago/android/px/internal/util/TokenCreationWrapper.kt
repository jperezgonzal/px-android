package com.mercadopago.android.px.internal.util

import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.addons.model.EscDeleteReason
import com.mercadopago.android.px.internal.callbacks.awaitTaggedCallback
import com.mercadopago.android.px.internal.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.internal.repository.CardTokenRepository
import com.mercadopago.android.px.internal.base.response.Response
import com.mercadopago.android.px.model.*
import com.mercadopago.android.px.model.exceptions.CardTokenException
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.model.Reason
import kotlin.coroutines.resume

internal class TokenCreationWrapper private constructor(builder: Builder) {

    private val cardTokenRepository: CardTokenRepository
    private val escManagerBehaviour: ESCManagerBehaviour
    private val card: Card?
    private val token: Token?
    private val paymentMethod: PaymentMethod
    private val reason: Reason

    init {
        cardTokenRepository = builder.cardTokenRepository
        escManagerBehaviour = builder.escManagerBehaviour
        card = builder.card
        token = builder.token
        paymentMethod = builder.paymentMethod!!
        reason = builder.reason!!
    }

    suspend fun createToken(cvv: String): Response<Token, MercadoPagoError> {
        return if (escManagerBehaviour.isESCEnabled) {
            createTokenWithEsc(cvv)
        } else {
            createTokenWithoutEsc(cvv)
        }
    }

    suspend fun createTokenWithEsc(cvv: String): Response<Token, MercadoPagoError> {
        return if (card != null) {
            SavedESCCardToken.createWithSecurityCode(card.id!!, cvv).run {
                validateSecurityCode(card)
                createESCToken(this).apply {
                    resolve(success = { token -> token.lastFourDigits = card.lastFourDigits })
                }
            }
        } else {
            SavedESCCardToken.createWithSecurityCode(token!!.cardId, cvv).run {
                validateCVVFromToken(cvv)
                createESCToken(this)
            }
        }
    }

    suspend fun createTokenWithoutEsc(cvv: String) =
        SavedCardToken(card!!.id, cvv).run {
            validateSecurityCode(card)
            createToken(this)
        }

    suspend fun cloneToken(cvv: String) = when (val response = doCloneToken()) {
        is Response.Success -> putCVV(cvv, response.result.id)
        is Response.Failure -> response
    }

    @Throws(CardTokenException::class)
    fun validateCVVFromToken(cvv: String): Boolean {
        if (token?.firstSixDigits.isNotNullNorEmpty()) {
            CardToken.validateSecurityCode(cvv, paymentMethod, token!!.firstSixDigits)
        } else if (!CardToken.validateSecurityCode(cvv)) {
            throw CardTokenException(CardTokenException.INVALID_FIELD)
        }
        return true
    }

    private suspend fun createESCToken(savedESCCardToken: SavedESCCardToken) = cardTokenRepository
        .createToken(savedESCCardToken)
        .awaitTaggedCallback(ApiUtil.RequestOrigin.CREATE_TOKEN).apply {
            resolve(success = {
                if (Reason.ESC_CAP == reason) { // Remove previous esc for tracking purpose
                    escManagerBehaviour.deleteESCWith(savedESCCardToken.cardId, EscDeleteReason.ESC_CAP, null)
                }
                cardTokenRepository.clearCap(savedESCCardToken.cardId) {}
            })
        }

    private suspend fun createToken(savedCardToken: SavedCardToken) = cardTokenRepository
        .createToken(savedCardToken)
        .awaitTaggedCallback(ApiUtil.RequestOrigin.CREATE_TOKEN)

    private suspend fun doCloneToken() = cardTokenRepository
        .cloneToken(token!!.id)
        .awaitTaggedCallback(ApiUtil.RequestOrigin.CREATE_TOKEN)

    private suspend fun putCVV(cvv: String, tokenId: String) = cardTokenRepository
        .putSecurityCode(cvv, tokenId)
        .awaitTaggedCallback(ApiUtil.RequestOrigin.CREATE_TOKEN)

    class Builder(val cardTokenRepository: CardTokenRepository, val escManagerBehaviour: ESCManagerBehaviour) {
        var card: Card? = null
            private set

        var token: Token? = null
            private set

        var paymentMethod: PaymentMethod? = null
            private set

        var reason: Reason? = Reason.NO_REASON
            private set

        fun with(card: Card) = apply {
            this.card = card
            this.paymentMethod = card.paymentMethod
        }

        fun with(token: Token) = apply { this.token = token }
        fun with(paymentMethod: PaymentMethod) = apply { this.paymentMethod = paymentMethod }
        fun with(paymentRecovery: PaymentRecovery) = apply {
            card = paymentRecovery.card
            token = paymentRecovery.token
            paymentMethod = paymentRecovery.paymentMethod
            reason = Reason.from(paymentRecovery)
        }

        fun build(): TokenCreationWrapper {
            check(!(token == null && card == null)) { "Token and card can't both be null" }

            checkNotNull(paymentMethod) { "Payment method not set" }

            return TokenCreationWrapper(this)
        }
    }
}