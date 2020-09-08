package com.mercadopago.android.px.internal.features.pay_button

import android.os.Parcel
import com.mercadopago.android.px.internal.util.KParcelable
import com.mercadopago.android.px.internal.util.parcelableCreator

data class RetriesConfiguration(
    val retriesMessage: String,
    val errorMessage: String,
    val actionErrorMessage: String,
    val maxRetries: Int): KParcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(retriesMessage)
        parcel.writeString(errorMessage)
        parcel.writeString(actionErrorMessage)
        parcel.writeInt(maxRetries)
    }

    companion object {
        @JvmField
        val CREATOR = parcelableCreator(::RetriesConfiguration)
    }
}