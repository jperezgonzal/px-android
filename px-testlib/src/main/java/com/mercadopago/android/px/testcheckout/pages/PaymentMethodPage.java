package com.mercadopago.android.px.testcheckout.pages;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

@Deprecated
public class PaymentMethodPage extends PageObject<CheckoutValidator> {

    protected PaymentMethodPage() {
    }

    public PaymentMethodPage(final CheckoutValidator validator) {
        super(validator);
    }

    //Payer access token 1 = "APP_USR-1505-080815-c6ea450de1bf828e39add499237d727f-312667294"
    public InstallmentsPage selectVisaCreditCardWithoutEsc(final String lastFourDigits) {
        return new InstallmentsPage(validator);
    }

    public ReviewAndConfirmPage selectAccountMoney() {
        return new ReviewAndConfirmPage(validator);
    }

    @Deprecated
    public SecurityCodePage selectSavedDebitCard() {
        return new SecurityCodePage(validator);
    }

    public SecurityCodePage selectSavedDebitCard(@NonNull final String lastFourDigits) {
        return new SecurityCodePage(validator);
    }

    public CardPage selectCard() {
        return new CardPage(validator);
    }

    @Deprecated
    public CreditCardPage selectCardWhenSavedPresent() {
        return new CreditCardPage(validator);
    }

    public CashPage selectCash() {
        return new CashPage(validator);
    }

    @Deprecated
    public ReviewAndConfirmPage selectTicketWithDefaultPayer(final int paymentMethodPosition) {
        return new ReviewAndConfirmPage(validator);
    }

    @Deprecated
    public PayerInformationPage selectTicketWithoutPayer(final int paymentMethodPosition) {
         return new PayerInformationPage(validator);
    }

    public ReviewAndConfirmPage selectTicketWithDefaultPayer(@NonNull final String paymentType) {
        return new ReviewAndConfirmPage(validator);
    }

    public PayerInformationIdentificationPage selectTicketWithoutPayer(@NonNull final String paymentType) {
        return new PayerInformationIdentificationPage(validator);
    }

    public InstallmentsPage selectPaymentMethodToInstallments(@NonNull final String paymentMethod) {
        return new InstallmentsPage(validator);
    }

    public SecurityCodePage selectPaymentMethodToSecurityCode(@NonNull final String paymentMethod) {
        return new SecurityCodePage(validator);
    }

    public ReviewAndConfirmPage selectPaymentMethodToReviewAndConfirm(@NonNull final String paymentMethod) {
        return new ReviewAndConfirmPage(validator);
    }

    public DiscountDetailPage pressOnDiscountDetail() {
        return new DiscountDetailPage(validator);
    }

    public OneTapPage pressBack() {
        return new OneTapPage(validator);
    }

    @Override
    public PaymentMethodPage validate(final CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }
}