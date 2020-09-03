package com.mercadopago.android.px.testcheckout.pages;

import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public class IssuerPage extends PageObject<CheckoutValidator> {

    public IssuerPage() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    protected IssuerPage(CheckoutValidator validator) {
        super(validator);
    }

    public ReviewAndConfirmPage enterBankOptionToReviewAndConfirm(final int bankOption) {
        return new ReviewAndConfirmPage(validator);
    }

    public InstallmentsPage enterBankOptionToInstallments(final int backOption) {
        return new InstallmentsPage(validator);
    }

    public CardAssociationResultSuccessPage enterBankOptionToCardAssociationResult(final int backOption) {
        return new CardAssociationResultSuccessPage(validator);
    }

    public PaymentMethodPage pressBack() {
        return new PaymentMethodPage(validator);
    }

    @Override
    public IssuerPage validate(CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }
}
