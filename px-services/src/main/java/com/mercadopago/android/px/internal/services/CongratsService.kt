package com.mercadopago.android.px.internal.services

import com.mercadopago.android.px.model.internal.CongratsResponse
import com.mercadopago.android.px.model.internal.remedies.RemediesBody
import com.mercadopago.android.px.model.internal.remedies.RemediesResponse
import retrofit2.http.*

interface CongratsService {
    @GET("/{version}/px_mobile/congrats")
    suspend fun getCongrats(
        @Path(value = "version", encoded = true) version: String,
        @Query("access_token") accessToken: String,
        @Query("payment_ids") paymentIds: String,
        @Query("platform") platform: String,
        @Query("campaign_id") campaignId: String,
        @Query("ifpe") turnedIFPECompliant: Boolean,
        @Query("payment_methods_ids") paymentMethodsIds: String,
        @Query("flow_name") flowName: String): CongratsResponse

    @POST("{environment}/px_mobile/v1/remedies/{payment_id}")
    suspend fun getRemedies(
        @Path(value = "environment", encoded = true) environment: String,
        @Path(value = "payment_id", encoded = true) paymentId: String,
        @Query("access_token") accessToken: String,
        @Query("one_tap") oneTap: Boolean,
        @Body body: RemediesBody): RemediesResponse
}