package com.mercadopago.android.px.testcheckout.assertions;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.test.espresso.matcher.ViewMatchers;
import com.mercadopago.android.px.testcheckout.idleresources.WaitForBusinessResult;
import com.mercadopago.android.px.testcheckout.idleresources.WaitForPaymentResult;
import com.mercadopago.android.px.testcheckout.pages.BusinessCongratsPage;
import com.mercadopago.android.px.testcheckout.pages.CallForAuthPage;
import com.mercadopago.android.px.testcheckout.pages.CardAssociationResultErrorPage;
import com.mercadopago.android.px.testcheckout.pages.CardAssociationResultSuccessPage;
import com.mercadopago.android.px.testcheckout.pages.CardPage;
import com.mercadopago.android.px.testcheckout.pages.CashPage;
import com.mercadopago.android.px.testcheckout.pages.CongratsPage;
import com.mercadopago.android.px.testcheckout.pages.CreditCardPage;
import com.mercadopago.android.px.testcheckout.pages.DebitCardPage;
import com.mercadopago.android.px.testcheckout.pages.DiscountDetailPage;
import com.mercadopago.android.px.testcheckout.pages.ExpiryDatePage;
import com.mercadopago.android.px.testcheckout.pages.ExpressPaymentPage;
import com.mercadopago.android.px.testcheckout.pages.IdentificationPage;
import com.mercadopago.android.px.testcheckout.pages.InstallmentsPage;
import com.mercadopago.android.px.testcheckout.pages.IssuerPage;
import com.mercadopago.android.px.testcheckout.pages.NamePage;
import com.mercadopago.android.px.testcheckout.pages.NoCheckoutPage;
import com.mercadopago.android.px.testcheckout.pages.OneTapPage;
import com.mercadopago.android.px.testcheckout.pages.PayerInformationBusinessNamePage;
import com.mercadopago.android.px.testcheckout.pages.PayerInformationFirstNamePage;
import com.mercadopago.android.px.testcheckout.pages.PayerInformationIdentificationPage;
import com.mercadopago.android.px.testcheckout.pages.PayerInformationLastNamePage;
import com.mercadopago.android.px.testcheckout.pages.PayerInformationPage;
import com.mercadopago.android.px.testcheckout.pages.PaymentMethodPage;
import com.mercadopago.android.px.testcheckout.pages.PaymentTypesPage;
import com.mercadopago.android.px.testcheckout.pages.PendingPage;
import com.mercadopago.android.px.testcheckout.pages.RejectedPage;
import com.mercadopago.android.px.testcheckout.pages.ReviewAndConfirmPage;
import com.mercadopago.android.px.testcheckout.pages.ReviewPaymentMethodsPage;
import com.mercadopago.android.px.testcheckout.pages.SecurityCodePage;
import com.mercadopago.android.px.testcheckout.pages.SecurityCodeToResultsPage;
import org.hamcrest.Matcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

public class DefaultValidator implements CheckoutValidator {

    private final WaitForPaymentResult waitForPaymentResult = new WaitForPaymentResult();

    @Override
    public void validate(@NonNull final IssuerPage issuerPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final NamePage namePage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final PaymentMethodPage paymentMethodPage) {
        //TODO fix, does not work
//        validateAmountView();
    }

    @Deprecated
    @Override
    public void validate(@NonNull final PayerInformationPage payerInformationPage) {
    }

    @Deprecated
    @CallSuper
    @Override
    public void validate(@NonNull final PayerInformationIdentificationPage page) {
    }

    @Deprecated
    @CallSuper
    @Override
    public void validate(@NonNull final PayerInformationBusinessNamePage page) {
    }

    @Deprecated
    @CallSuper
    @Override
    public void validate(@NonNull final PayerInformationFirstNamePage page) {
    }

    @Deprecated
    @CallSuper
    @Override
    public void validate(@NonNull final PayerInformationLastNamePage page) {
    }

    @Deprecated
    @Override
    public void validate(@NonNull final ReviewAndConfirmPage reviewAndConfirmPage) {
    }

    @Override
    public void validate(@NonNull final SecurityCodePage securityCodePage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final NoCheckoutPage noCheckoutPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final CardPage cardPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final CashPage cashPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final CreditCardPage creditCardPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final DebitCardPage debitCardPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final ExpiryDatePage expiryDatePage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final IdentificationPage identificationPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final InstallmentsPage installmentsPage) {
        //TODO fix, does not work
//        validateAmountView();
    }

    @Override
    public void validate(@NonNull final ReviewPaymentMethodsPage reviewPaymentMethodsPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final DiscountDetailPage discountDetailPage) {
        final Matcher<View> summary = withId(com.mercadopago.android.px.R.id.summary);
        final Matcher<View> description = withId(com.mercadopago.android.px.R.id.description);
        onView(summary).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(description).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));


        onView(description)
            .check(matches(withText(com.mercadopago.android.px.R.string.px_we_apply_the_best_available_discount)));
    }

    @Override
    public void validate(@NonNull final SecurityCodeToResultsPage securityCodeToResultsPage) {
        //TODO implement default PX Validations
    }

    @Deprecated
    @Override
    public void validate(@NonNull final OneTapPage oneTapPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final CallForAuthPage callForAuthPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final CongratsPage congratsPage) {
        waitForPaymentResult.start();
        assertTrue(congratsPage.isSuccess());
        waitForPaymentResult.stop();

    }

    public void validate(@NonNull final BusinessCongratsPage congratsPage) {
        final WaitForBusinessResult result = new WaitForBusinessResult();
        result.start();
        assertTrue(congratsPage.isSuccess());
        result.stop();
    }

    @Override
    public void validate(@NonNull final PendingPage pendingPage) {
        waitForPaymentResult.start();
        assertTrue(pendingPage.isPending());
        waitForPaymentResult.stop();    }

    @Override
    public void validate(@NonNull final RejectedPage rejectedPage) {
        waitForPaymentResult.start();
        assertTrue(rejectedPage.isError());
        waitForPaymentResult.stop();
    }

    @Override
    public void validate(@NonNull final ExpressPaymentPage expressPaymentPage) {
        //TODO implement default PX Validations
    }

    @Deprecated
    @CallSuper
    @Override
    public void validate(@NonNull final CardAssociationResultSuccessPage cardAssociationResultSuccessPage) {
    }

    @Deprecated
    @Override
    public void validate(@NonNull final CardAssociationResultErrorPage cardAssociationResultErrorPage) {
    }

    @Deprecated
    @CallSuper
    @Override
    public void validate(@NonNull final PaymentTypesPage paymentTypesPage) {
    }
}