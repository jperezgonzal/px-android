package com.mercadopago.android.px.internal.features.checkout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadolibre.android.cardform.internal.LifecycleListener;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

/* default */ interface Checkout {
    /* default */ interface View extends MvpView {

        void showError(final MercadoPagoError error);

        void showProgress();

        void finishWithPaymentResult(@Nullable final Integer customResultCode, @Nullable final Payment payment);

        void cancelCheckout();

        void showOneTap();

        void hideProgress();

        void showFailureRecoveryError();

        void goToLink(@NonNull final String link);

        void openInWebView(@NonNull final String link);
    }

    /* default */ interface Actions {
        void initialize();

        void onErrorCancel(@Nullable final MercadoPagoError mercadoPagoError);

        void recoverFromFailure();

        void onPaymentResultResponse(@Nullable final Integer customResultCode);

        void onCardAdded(@NonNull final String cardId, @NonNull final LifecycleListener.Callback callback);
    }
}