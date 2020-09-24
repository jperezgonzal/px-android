package com.mercadopago.android.px.internal.features.security_code.model

import android.os.Parcel
import com.mercadopago.android.px.internal.features.express.RenderMode
import com.mercadopago.android.px.internal.util.KParcelable
import com.mercadopago.android.px.internal.util.parcelableCreator
import com.mercadopago.android.px.model.PaymentRecovery
import com.mercadopago.android.px.model.internal.PaymentConfiguration
import com.mercadopago.android.px.tracking.internal.model.Reason

data class SecurityCodeParams(
    val paymentConfiguration: PaymentConfiguration,
    val fragmentContainer: Int,
    val renderMode: RenderMode,
    val paymentRecovery: PaymentRecovery? = null,
    val reason: Reason? = null
) : KParcelable {

    constructor(parcel: Parcel): this(
        parcel.readParcelable(PaymentConfiguration::class.java.classLoader)!!,
        parcel.readInt(),
        RenderMode.valueOf(parcel.readString()!!),
        parcel.readParcelable(PaymentRecovery::class.java.classLoader),
        parcel.readString()?.let { Reason.valueOf(it) }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(paymentConfiguration, flags)
        parcel.writeInt(fragmentContainer)
        parcel.writeString(renderMode.name)
        parcel.writeParcelable(paymentRecovery, flags)
        parcel.writeString(reason?.name)
    }

    companion object {
        @JvmField val CREATOR = parcelableCreator(::SecurityCodeParams)
    }
}