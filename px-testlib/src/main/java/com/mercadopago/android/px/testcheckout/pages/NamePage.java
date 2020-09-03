package com.mercadopago.android.px.testcheckout.pages;

import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public class NamePage extends PageObject<CheckoutValidator> {

    public NamePage() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public NamePage(CheckoutValidator validator) {
        super(validator);
    }

    @Override
    public NamePage validate(CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }

    public ExpiryDatePage enterCardholderName(final String cardHolderName) {
        return new ExpiryDatePage(validator);
    }

    public NoCheckoutPage pressBackWithExclusion() {
        return new NoCheckoutPage(validator);
    }

    public PaymentMethodPage pressBack() {
        return new PaymentMethodPage(validator);
    }

    public CreditCardPage pressPrevious() {
        return new CreditCardPage(validator);
    }
}
