package com.mercadopago.android.px.testcheckout.pages;

import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public class InstallmentsPage extends PageObject<CheckoutValidator> {

    public InstallmentsPage() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    protected InstallmentsPage(CheckoutValidator validator) {
        super(validator);
    }

    @Override
    public InstallmentsPage validate(CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }

    public PaymentMethodPage pressBack() {
        return new PaymentMethodPage(validator);
    }

    public ReviewAndConfirmPage selectInstallments(int installmentsOption) {
        return new ReviewAndConfirmPage(validator);
    }

    public SecurityCodePage selectInstallmentsForSavedCard(int installmentsOption) {
        return new SecurityCodePage(validator);
    }

    public ReviewAndConfirmPage selectInstallmentsForSavedCardWithEsc(int installmentsOption) {
        return new ReviewAndConfirmPage(validator);
    }

    public DiscountDetailPage pressOnDiscountDetail() {
        return new DiscountDetailPage(validator);
    }
}
