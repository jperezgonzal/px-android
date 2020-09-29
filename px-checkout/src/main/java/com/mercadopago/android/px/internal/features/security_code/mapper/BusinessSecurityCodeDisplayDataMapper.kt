package com.mercadopago.android.px.internal.features.security_code.mapper

import com.mercadopago.android.px.internal.features.security_code.data.SecurityCodeDisplayData
import com.mercadopago.android.px.internal.features.security_code.domain.model.BusinessCardDisplayInfo
import com.mercadopago.android.px.internal.features.security_code.domain.model.BusinessSecurityCodeDisplayData
import com.mercadopago.android.px.internal.features.security_code.domain.model.BusinessVirtualCardInfo
import com.mercadopago.android.px.internal.viewmodel.mappers.Mapper

internal class BusinessSecurityCodeDisplayDataMapper :
    Mapper<SecurityCodeDisplayData, BusinessSecurityCodeDisplayData>() {

    override fun map(model: SecurityCodeDisplayData) = model.run {

        val businessCardDisplayInfo = cardDisplayInfo?.let { displayInfo ->
            BusinessCardDisplayInfo(
                displayInfo.cardholderName,
                displayInfo.expiration,
                displayInfo.color,
                displayInfo.fontColor,
                displayInfo.issuerId,
                displayInfo.cardPattern,
                displayInfo.getCardPattern(),
                displayInfo.lastFourDigits,
                displayInfo.paymentMethodImage,
                displayInfo.issuerImage,
                displayInfo.fontType,
                displayInfo.paymentMethodImageUrl,
                displayInfo.issuerImageUrl
            )
        }

        val businessVirtualCardInfo = virtualCardInfo?.let { virtualCardInfo ->
            BusinessVirtualCardInfo(virtualCardInfo.title, virtualCardInfo.message)
        }

        BusinessSecurityCodeDisplayData(
            businessCardDisplayInfo,
            businessVirtualCardInfo,
            securityCodeLength)
    }
}