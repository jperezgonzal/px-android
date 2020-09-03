package com.mercadopago.android.px.testcheckout.pages;

import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public class CardAssociationResultErrorPage extends PageObject<CheckoutValidator> {

    public CardAssociationResultErrorPage(final CheckoutValidator validator) {
        super(validator);
    }

    @Override
    public PageObject validate(final CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }
}
