package com.mercadopago.android.px.testcheckout.pages;

import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public class IdentificationPage extends PageObject<CheckoutValidator> {

    public IdentificationPage() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public IdentificationPage(CheckoutValidator validator) {
        super(validator);
    }

    // Case credit card - a way to resolve this is with a card type
    public InstallmentsPage enterIdentificationNumberToInstallments(final String idNumber) {
        return new InstallmentsPage(validator);
    }

    // Case debit card - a way to resolve this is with a card type

    public ReviewAndConfirmPage enterIdentificationNumberToReviewAndConfirm(final String idNumber) {
        return new ReviewAndConfirmPage(validator);
    }

    public IssuerPage enterIdentificationNumberToIssuer(final String idNumber) {
        return new IssuerPage(validator);
    }

    public CardAssociationResultSuccessPage enterIdentificationNumberToCardAssociationResultSuccessPage(
        final String idNumber) {
        return new CardAssociationResultSuccessPage(validator);
    }

    public PaymentTypesPage enterIdentificationNumberToPaymentTypesPage(
        final String idNumber) {
        return new PaymentTypesPage(validator);
    }

    public NoCheckoutPage enterIdentificationNumberToNoCheckoutPage(
        final String idNumber) {
        return new NoCheckoutPage(validator);
    }

    @Override
    public IdentificationPage validate(CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }

    public NoCheckoutPage pressBackWithExclusions() {
        return new NoCheckoutPage(validator);
    }

    public PaymentMethodPage pressBack() {
        return new PaymentMethodPage(validator);
    }

    public SecurityCodePage pressPrevious() {
        return new SecurityCodePage(validator);
    }
}
