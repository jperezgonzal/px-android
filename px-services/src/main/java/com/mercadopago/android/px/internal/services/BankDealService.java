package com.mercadopago.android.px.internal.services;

import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.model.BankDeal;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BankDealService {

    @GET("/v1/payment_methods/deals")
    MPCall<List<BankDeal>> getBankDeals(@Query("public_key") String publicKey,
        @Nullable @Query("access_token") String privateKey,
        @Query("locale") String locale);
}