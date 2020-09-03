package com.mercadopago.android.px.testcheckout.pages;

import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public class ReviewPaymentMethodsPage extends PageObject<CheckoutValidator> {

    public ReviewPaymentMethodsPage() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public ReviewPaymentMethodsPage(CheckoutValidator validator) {
        super(validator);
    }

    @Override
    public PageObject validate(final CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }

    public CreditCardPage clickEnterCardButton() {
        return new CreditCardPage(validator);
    }
}
