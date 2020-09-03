package com.mercadopago.android.px.testcheckout.pages;

import androidx.annotation.NonNull;
import android.view.View;
import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;
import org.hamcrest.Matcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class SecurityCodeToResultsPage extends PageObject<CheckoutValidator> {

    public SecurityCodeToResultsPage() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public SecurityCodeToResultsPage(@NonNull final CheckoutValidator validator) {
        super(validator);
    }

    public CongratsPage enterSecurityCodeToCongratsPage(final String cvv) {
        enterSecurityCode(cvv);
        return new CongratsPage(validator);
    }

    public PendingPage enterSecurityCodeToPendingPage(final String cvv) {
        enterSecurityCode(cvv);
        return new PendingPage(validator);
    }

    public CallForAuthPage enterSecurityCodeToCallForAuthPage(final String cvv) {
        enterSecurityCode(cvv);
        return new CallForAuthPage(validator);
    }

    @Override
    public SecurityCodeToResultsPage validate(final CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }

    private void enterSecurityCode(final String escNumber) {
        Matcher<View> cardSecurityCodeEditTextMatcher = withId(com.mercadopago.android.px.R.id.mpsdkCardSecurityCode);
        Matcher<View> cardNextButtonTextMatcher = withId(com.mercadopago.android.px.R.id.mpsdkNextButton);
        onView(cardSecurityCodeEditTextMatcher).perform(typeText(escNumber));
        onView(cardNextButtonTextMatcher).perform(click());
    }
}