package com.mercadopago.android.px.testcheckout.pages;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public final class PayerInformationLastNamePage extends PageObject<CheckoutValidator> {

    public PayerInformationLastNamePage(final CheckoutValidator validator) {
        super(validator);
    }

    public PayerInformationLastNamePage enterLastName(@NonNull final String lastName) {
        return new PayerInformationLastNamePage(validator);
    }

    public ReviewAndConfirmPage pressNextButton() {
        return new ReviewAndConfirmPage(validator);
    }

    @Override
    public PayerInformationLastNamePage validate(final CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }

    public PaymentMethodPage pressBack() {
        return new PaymentMethodPage(validator);
    }
}