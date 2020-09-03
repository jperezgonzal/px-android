package com.mercadopago.android.px.testcheckout.pages;

import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public class CreditCardPage extends PageObject<CheckoutValidator> {

    public CreditCardPage() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public CreditCardPage(final CheckoutValidator validator) {
        super(validator);
    }

    @Override
    public CreditCardPage validate(CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }

    public NamePage enterCreditCardNumber(final String cardNumber) {
        return new NamePage(validator);
    }

    public NoCheckoutPage pressBackWithExclusion() {
        return new NoCheckoutPage(validator);
    }

    public PaymentMethodPage pressBack() {
        return new PaymentMethodPage(validator);
    }

    public CreditCardPage enterExcludedCreditCardNumber(final String cardNumber) {
        return new CreditCardPage(validator);
    }

    public ReviewPaymentMethodsPage clickPaymentMethodNotSupportedSnackbar() {
        return new ReviewPaymentMethodsPage(validator);
    }
}
