package com.mercadopago.android.px.testcheckout.pages;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public final class PayerInformationIdentificationPage extends PageObject<CheckoutValidator> {

    public PayerInformationIdentificationPage(final CheckoutValidator validator) {
        super(validator);
    }

    public PayerInformationIdentificationPage enterIdentificationTypeAndNumber(@NonNull final String idType,
        @NonNull final String idNumber) {
        return new PayerInformationIdentificationPage(validator);
    }

    public PayerInformationFirstNamePage pressNextButtonToFirstNamePage() {
        return new PayerInformationFirstNamePage(validator);
    }

    public PayerInformationBusinessNamePage pressNextButtonToBusinesstNamePage() {
        return new PayerInformationBusinessNamePage(validator);
    }

    @Override
    public PayerInformationIdentificationPage validate(final CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }

    public PaymentMethodPage pressBack() {
        return new PaymentMethodPage(validator);
    }
}