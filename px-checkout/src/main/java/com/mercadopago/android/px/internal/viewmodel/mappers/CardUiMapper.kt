package com.mercadopago.android.px.internal.viewmodel.mappers

import com.mercadopago.android.px.internal.features.security_code.domain.model.BusinessCardDisplayInfo
import com.mercadopago.android.px.internal.viewmodel.CardUiConfiguration
import com.mercadopago.android.px.internal.viewmodel.DisableConfiguration
import com.mercadopago.android.px.model.CardDisplayInfo

internal class CardUiMapper(private val disableConfiguration: DisableConfiguration? = null) {

    fun map(cardDisplayInfo: BusinessCardDisplayInfo): CardUiConfiguration {
        with(cardDisplayInfo) {
            return CardUiConfiguration(
                cardholderName,
                expiration,
                cardPatternMask,
                issuerImageUrl,
                paymentMethodImageUrl,
                fontType,
                cardPattern,
                color,
                fontColor,
                disableConfiguration
            )
        }
    }

    fun map(cardDisplayInfo: CardDisplayInfo): CardUiConfiguration {
        with(cardDisplayInfo) {
            return CardUiConfiguration(
                cardholderName,
                expiration,
                getCardPattern(),
                issuerImageUrl,
                paymentMethodImageUrl,
                fontType,
                cardPattern,
                color,
                fontColor,
                disableConfiguration
            )
        }
    }
}