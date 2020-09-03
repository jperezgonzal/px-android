package com.mercadopago.android.px.testcheckout.pages;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public class PayerInformationPage extends PageObject<CheckoutValidator> {

    public PayerInformationPage() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public PayerInformationPage(final CheckoutValidator validator) {
        super(validator);
    }

    // Case debit card - a way to resolve this is with a card type
    public PayerInformationPage enterIdentificationTypeAndNumberAndPressNext(@NonNull final String idType,
        @NonNull final String idNumber) {
        return new PayerInformationPage(validator);
    }

    public PayerInformationPage enterFirstNameAndPressNext(@NonNull final String firstName) {
        return new PayerInformationPage(validator);
    }

    public ReviewAndConfirmPage enterLastNameAndPressNext(@NonNull final String lastName) {
        return new ReviewAndConfirmPage(validator);
    }

    public ReviewAndConfirmPage enterBusinessNameAndPressNext(@NonNull final String businessName) {
        return new ReviewAndConfirmPage(validator);
    }

    @Override
    public PayerInformationPage validate(CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }

    public PaymentMethodPage pressBack() {
        return new PaymentMethodPage(validator);
    }
}