package com.mercadopago.android.px.internal.features.pay_button

import android.os.Parcel
import com.mercadopago.android.px.internal.util.KParcelable
import com.mercadopago.android.px.internal.util.parcelableCreator

data class SnackBarErrorConfiguration(
    val errorMessage: String,
    val actionErrorMessage: String
): KParcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(errorMessage)
        parcel.writeString(actionErrorMessage)
    }

    companion object {
        @JvmField val CREATOR  = parcelableCreator(::SnackBarErrorConfiguration)
    }
}