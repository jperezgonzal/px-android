package com.mercadopago.android.px.utils;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.ExitAction;
import com.mercadopago.android.px.model.GenericPayment;
import com.mercadopago.android.px.model.IParcelablePaymentDescriptor;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.internal.GenericPaymentDescriptor;
import com.mercadopago.example.R;

public final class PaymentUtils {

    private PaymentUtils() {
    }

    @NonNull
    public static BusinessPayment getBusinessPaymentApproved() {
        return new BusinessPayment.Builder(BusinessPayment.Decorator.APPROVED, Payment.StatusCodes.STATUS_APPROVED,
            Payment.StatusDetail.STATUS_DETAIL_ACCREDITED,
            R.drawable.px_icon_card, "Title")
            .setPrimaryButton(new ExitAction("Button Name", 23))
            .setReceiptId("123456")
            .build();
    }

    @NonNull
    public static IParcelablePaymentDescriptor getGenericPaymentApproved() {
        return GenericPaymentDescriptor.with(new GenericPayment.Builder(
            Payment.StatusCodes.STATUS_APPROVED,
            Payment.StatusDetail.STATUS_DETAIL_ACCREDITED
        ).setPaymentId(123L).createGenericPayment());
    }

    @NonNull
    public static IParcelablePaymentDescriptor getGenericPaymentRejected() {
        return GenericPaymentDescriptor.with(new GenericPayment.Builder(
            Payment.StatusCodes.STATUS_REJECTED,
            Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE
        ).setPaymentId(6101162949L).createGenericPayment());
    }
}