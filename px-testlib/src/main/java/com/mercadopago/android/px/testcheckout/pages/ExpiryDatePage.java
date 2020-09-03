package com.mercadopago.android.px.testcheckout.pages;

import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public class ExpiryDatePage extends PageObject<CheckoutValidator> {

    public ExpiryDatePage() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public ExpiryDatePage(CheckoutValidator validator) {
        super(validator);
    }

    @Override
    public ExpiryDatePage validate(CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }

    public SecurityCodePage enterExpiryDate(final String s) {
        return new SecurityCodePage(validator);
    }

    public NoCheckoutPage pressBackWithExclusion() {
        return new NoCheckoutPage(validator);
    }

    public PaymentMethodPage pressBack() {
        return new PaymentMethodPage(validator);
    }

    public NamePage pressPrevious() {
        return new NamePage(validator);
    }
}
