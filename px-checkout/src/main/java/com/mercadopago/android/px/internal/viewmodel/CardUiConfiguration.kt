package com.mercadopago.android.px.internal.viewmodel

import android.graphics.Color
import android.widget.ImageView
import com.meli.android.carddrawer.configuration.FontType
import com.meli.android.carddrawer.configuration.SecurityCodeLocation
import com.meli.android.carddrawer.model.CardAnimationType
import com.meli.android.carddrawer.model.CardUI
import com.mercadopago.android.px.internal.features.security_code.domain.model.BusinessCardDisplayInfo
import com.mercadopago.android.px.internal.util.ViewUtils

internal class CardUiConfiguration(
    private val businessCardDisplayInfo: BusinessCardDisplayInfo,
    private val disableConfiguration: DisableConfiguration? = null,
    val disabled: Boolean = false) : CardUI {

    val name: String

    val date: String

    val number: String

    init {
        with(businessCardDisplayInfo) {
            name = cardholderName
            date = expiration
            number = cardPatternMask
        }
    }

    override fun getBankImageRes(): Int = 0

    override fun setBankImage(bankImage: ImageView) {
        toGrayScaleIfDisabled(bankImage)
    }

    override fun setCardLogoImage(cardLogo: ImageView) {
        toGrayScaleIfDisabled(cardLogo)
    }

    override fun getBankImageUrl(): String? = businessCardDisplayInfo.issuerImageUrl

    override fun getCardLogoImageUrl(): String? = businessCardDisplayInfo.paymentMethodImageUrl

    override fun getAnimationType(): String = CardAnimationType.NONE

    override fun getFontType(): String = if (disabled) {
        FontType.NONE
    } else {
        businessCardDisplayInfo.fontType ?: FontType.LIGHT_TYPE
    }

    override fun getCardLogoImageRes(): Int = 0

    override fun getSecurityCodePattern(): Int = NUMBER_SEC_CODE

    override fun getExpirationPlaceHolder(): String = ""

    override fun getSecurityCodeLocation(): String = SecurityCodeLocation.BACK

    override fun getCardNumberPattern(): IntArray = businessCardDisplayInfo.cardPattern

    override fun getNamePlaceHolder(): String = ""

    override fun getCardBackgroundColor(): Int = if (disabled && disableConfiguration != null)
        disableConfiguration.backgroundColor
    else Color.parseColor(businessCardDisplayInfo.color)

    override fun getCardFontColor(): Int = if (disabled && disableConfiguration != null)
        disableConfiguration.fontColor
    else Color.parseColor(businessCardDisplayInfo.fontColor)

    private fun toGrayScaleIfDisabled(imageView: ImageView) {
        if (disabled) {
            ViewUtils.grayScaleView(imageView)
        } else {
            imageView.clearColorFilter()
        }
    }

    companion object {
        private const val NUMBER_SEC_CODE = 3
    }
}