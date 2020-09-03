package com.mercadopago.android.px.testcheckout.pages;

import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public class CashPage extends PageObject<CheckoutValidator> {

    public CashPage() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public CashPage(CheckoutValidator validator) {
        super(validator);
    }

    public ReviewAndConfirmPage selectMethod(final String paymentMethodName) {
        return new ReviewAndConfirmPage(validator);
    }

    @Override
    public CashPage validate(final CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }
}
