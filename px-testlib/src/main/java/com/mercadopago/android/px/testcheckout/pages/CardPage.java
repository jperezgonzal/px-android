package com.mercadopago.android.px.testcheckout.pages;

import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public class CardPage extends PageObject<CheckoutValidator> {

    public CardPage() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public CardPage(final CheckoutValidator validator) {
        super(validator);
    }

    public CreditCardPage selectCreditCard() {
        return new CreditCardPage(validator);
    }

    public DebitCardPage selectDebitCard() {
        return new DebitCardPage(validator);
    }

    @Override
    public CardPage validate(final CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }
}