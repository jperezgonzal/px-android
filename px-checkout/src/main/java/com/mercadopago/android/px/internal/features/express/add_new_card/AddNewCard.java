package com.mercadopago.android.px.internal.features.express.add_new_card;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.util.CardFormWithFragmentWrapper;

/* default */ interface AddNewCard {

    interface View extends MvpView {
        void startCardForm(@NonNull final CardFormWithFragmentWrapper cardFormWithFragmentWrapper);
    }

    interface Actions {
        void onAddNewCardSelected();
    }
}