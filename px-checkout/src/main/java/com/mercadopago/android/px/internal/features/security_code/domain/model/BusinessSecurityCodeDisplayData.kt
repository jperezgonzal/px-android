package com.mercadopago.android.px.internal.features.security_code.domain.model

internal data class BusinessSecurityCodeDisplayData(
    val cardDisplayInfo: BusinessCardDisplayInfo?,
    val virtualCardInfo: BusinessVirtualCardInfo?,
    val securityCodeLength: Int
)