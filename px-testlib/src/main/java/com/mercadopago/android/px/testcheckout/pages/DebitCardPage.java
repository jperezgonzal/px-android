package com.mercadopago.android.px.testcheckout.pages;

import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public class DebitCardPage extends PageObject<CheckoutValidator> {

    public DebitCardPage() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public DebitCardPage(CheckoutValidator validator) {
        super(validator);
    }

    @Override
    public DebitCardPage validate(CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }

    public NamePage enterCreditCardNumber(final String cardNumber) {
        return new NamePage(validator);
    }
}
