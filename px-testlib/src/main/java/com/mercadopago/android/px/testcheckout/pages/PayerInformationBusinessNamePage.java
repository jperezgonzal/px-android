package com.mercadopago.android.px.testcheckout.pages;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public final class PayerInformationBusinessNamePage extends PageObject<CheckoutValidator> {

    public PayerInformationBusinessNamePage(final CheckoutValidator validator) {
        super(validator);
    }

    public PayerInformationBusinessNamePage enterBusinessName(@NonNull final String businessName) {
        return new PayerInformationBusinessNamePage(validator);
    }

    public ReviewAndConfirmPage pressNextButton() {
        return new ReviewAndConfirmPage(validator);
    }

    @Override
    public PayerInformationBusinessNamePage validate(final CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }

    public PaymentMethodPage pressBack() {
        return new PaymentMethodPage(validator);
    }
}