package com.mercadopago.android.px.testcheckout.pages;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public class ReviewAndConfirmPage extends PageObject<CheckoutValidator> {

    public ReviewAndConfirmPage() {
    }

    public ReviewAndConfirmPage(final CheckoutValidator validator) {
        super(validator);
    }

    public CongratsPage pressConfirmButton() {
        return new CongratsPage(validator);
    }

    public BusinessCongratsPage pressConfirmButtonforBusiness() {
        return new BusinessCongratsPage(validator);
    }

    public SecurityCodePage pressConfirmButtonWithInvalidEsc() {
        return new SecurityCodePage(validator);
    }

    public RejectedPage pressConfirmButtonAndReject() {
        return new RejectedPage(validator);
    }

    public PendingPage pressConfirmButtonAndPending() {
        return new PendingPage(validator);
    }

    @Override
    public ReviewAndConfirmPage validate(final CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }

    public NoCheckoutPage pressBackWithExclusion() {
        return new NoCheckoutPage(validator);
    }

    public PaymentMethodPage pressBack() {
        return new PaymentMethodPage(validator);
    }

    @NonNull
    public PaymentMethodPage clickChangePaymentMethod() {
        return new PaymentMethodPage(validator);
    }

    /**
     * @deprecated use clickModifyPayerInformation instead.
     */
    @Deprecated
    @NonNull
    public PayerInformationPage pressModifyPayerInformation() {
        return new PayerInformationPage(validator);
    }

    /**
     * @deprecated no longer able to modify payer information
     */
    @Deprecated
    @NonNull
    public PayerInformationIdentificationPage clickModifyPayerInformation() {
        return new PayerInformationIdentificationPage(validator);
    }
}