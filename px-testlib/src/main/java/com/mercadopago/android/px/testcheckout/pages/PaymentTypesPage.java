package com.mercadopago.android.px.testcheckout.pages;

import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public class PaymentTypesPage extends PageObject<CheckoutValidator> {

    public PaymentTypesPage() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public PaymentTypesPage(CheckoutValidator validator) {
        super(validator);
    }

    public CardAssociationResultSuccessPage selectDebitCardTypeToCardAssociationResultSuccessPage() {
        return new CardAssociationResultSuccessPage(validator);
    }

    public CardAssociationResultSuccessPage selectCreditCardTypeToCardAssociationResultSuccessPage() {
        return new CardAssociationResultSuccessPage(validator);
    }

    @Override
    public PaymentTypesPage validate(CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }
}
