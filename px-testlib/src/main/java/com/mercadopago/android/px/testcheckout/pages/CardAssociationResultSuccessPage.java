package com.mercadopago.android.px.testcheckout.pages;

import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public class CardAssociationResultSuccessPage extends PageObject<CheckoutValidator> {

    public CardAssociationResultSuccessPage(final CheckoutValidator validator) {
        super(validator);
    }

    @Override
    public PageObject validate(final CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }
}
