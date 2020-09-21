package com.mercadopago.android.px.internal.features.security_code.data

import com.mercadopago.android.px.model.CardDisplayInfo
import com.mercadopago.android.px.model.CvvInfo

internal data class SecurityCodeDisplayData(
    val cardDisplayInfo: CardDisplayInfo?,
    val virtualCardInfo: CvvInfo?,
    val securityCodeLength: Int
)