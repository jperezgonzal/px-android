package com.mercadopago.android.px.testcheckout.pages;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public final class PayerInformationFirstNamePage extends PageObject<CheckoutValidator> {

    public PayerInformationFirstNamePage(final CheckoutValidator validator) {
        super(validator);
    }

    public PayerInformationFirstNamePage enterFirstName(@NonNull final String firstName) {
        return new PayerInformationFirstNamePage(validator);
    }

    public PayerInformationLastNamePage pressNextButton() {
        return new PayerInformationLastNamePage(validator);
    }

    @Override
    public PayerInformationFirstNamePage validate(final CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }

    public PaymentMethodPage pressBack() {
        return new PaymentMethodPage(validator);
    }
}