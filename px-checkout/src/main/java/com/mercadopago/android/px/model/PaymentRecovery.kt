package com.mercadopago.android.px.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

//TODO move to internal package.
class PaymentRecovery(private val statusDetail: String, val token: Token?, val card: Card?,
                      val paymentMethod: PaymentMethod?) : Serializable, Parcelable {

    @Deprecated("")
    constructor(paymentStatusDetail: String) : this(paymentStatusDetail, null, null)

    @Deprecated("")
    constructor(statusDetail: String, token: Token) : this(statusDetail, token, null)

    @Deprecated("")
    constructor(statusDetail: String, token: Token?, card: Card?) : this(statusDetail, token, card, null)

    val isTokenRecoverable: Boolean
        get() = Payment.StatusDetail.isStatusDetailRecoverable(statusDetail)

    val isStatusDetailCallForAuthorize: Boolean
        get() = Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE == statusDetail

    val isStatusDetailCardDisabled: Boolean
        get() = Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED == statusDetail

    val isStatusDetailInvalidESC: Boolean
        get() = Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC == statusDetail

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readSerializable() as Token,
            parcel.readParcelable(Card::class.java.classLoader),
            parcel.readParcelable(PaymentMethod::class.java.classLoader))

    fun shouldAskForCvv(): Boolean {
        return Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED != statusDetail
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(statusDetail)
        parcel.writeSerializable(token)
        parcel.writeParcelable(card, flags)
        parcel.writeParcelable(paymentMethod, flags)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<PaymentRecovery> {
        override fun createFromParcel(parcel: Parcel) = PaymentRecovery(parcel)
        override fun newArray(size: Int) = arrayOfNulls<PaymentRecovery>(size)
    }
}