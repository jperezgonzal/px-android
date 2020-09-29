package com.mercadopago.android.px.preferences

import java.io.Serializable

data class PostPaymentUrls(
    val failure: String?,
    val pending: String?,
    val success: String?
) : Serializable