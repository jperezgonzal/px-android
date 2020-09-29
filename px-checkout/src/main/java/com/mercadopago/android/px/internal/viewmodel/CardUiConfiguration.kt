package com.mercadopago.android.px.internal.viewmodel

import android.graphics.Color
import android.os.Parcel
import android.widget.ImageView
import com.meli.android.carddrawer.configuration.FontType
import com.meli.android.carddrawer.configuration.SecurityCodeLocation
import com.meli.android.carddrawer.model.CardAnimationType
import com.meli.android.carddrawer.model.CardUI
import com.mercadopago.android.px.internal.util.KParcelable
import com.mercadopago.android.px.internal.util.ViewUtils
import com.mercadopago.android.px.internal.util.parcelableCreator

internal class CardUiConfiguration(
    val name: String,
    val date: String,
    val number: String,
    private val issuerImageUrl: String?,
    private val paymentMethodImageUrl: String?,
    private val fontType: String?,
    private val cardPattern: IntArray,
    private val color: String,
    private val fontColor: String,
    private val disableConfiguration: DisableConfiguration? = null,
    private var disabled: Boolean = false) : CardUI, KParcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createIntArray()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(DisableConfiguration::class.java.classLoader),
        parcel.readByte() != 0.toByte())

    override fun getBankImageRes(): Int = 0

    override fun setBankImage(bankImage: ImageView) {
        toGrayScaleIfDisabled(bankImage)
    }

    override fun setCardLogoImage(cardLogo: ImageView) {
        toGrayScaleIfDisabled(cardLogo)
    }

    override fun getBankImageUrl(): String? = issuerImageUrl

    override fun getCardLogoImageUrl(): String? = paymentMethodImageUrl

    override fun getAnimationType(): String = CardAnimationType.NONE

    override fun getFontType(): String = if (disabled) { FontType.NONE } else { fontType ?: FontType.LIGHT_TYPE }

    override fun getCardLogoImageRes(): Int = 0

    override fun getSecurityCodePattern(): Int = NUMBER_SEC_CODE

    override fun getExpirationPlaceHolder(): String = ""

    override fun getSecurityCodeLocation(): String = SecurityCodeLocation.BACK

    override fun getCardNumberPattern(): IntArray = cardPattern

    override fun getNamePlaceHolder(): String = ""

    override fun getCardBackgroundColor(): Int = if (disabled && disableConfiguration != null)
        disableConfiguration.backgroundColor
    else Color.parseColor(color)

    override fun getCardFontColor(): Int = if (disabled && disableConfiguration != null)
        disableConfiguration.fontColor
    else Color.parseColor(fontColor)

    private fun toGrayScaleIfDisabled(imageView: ImageView) {
        if (disabled) {
            ViewUtils.grayScaleView(imageView)
        } else {
            imageView.clearColorFilter()
        }
    }

    fun disable() {
        disabled = true
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(date)
        parcel.writeString(number)
        parcel.writeString(issuerImageUrl)
        parcel.writeString(paymentMethodImageUrl)
        parcel.writeString(fontType)
        parcel.writeIntArray(cardPattern)
        parcel.writeString(color)
        parcel.writeString(fontColor)
        parcel.writeParcelable(disableConfiguration, flags)
        parcel.writeByte(if (disabled) 1 else 0)
    }

    override fun describeContents() = 0

    companion object {
        @JvmField
        val CREATOR = parcelableCreator(::CardUiConfiguration)
        private const val NUMBER_SEC_CODE = 3
    }
}