package com.mercadopago.android.px.internal.features.checkout;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.repository.InitRepository;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.mocks.InitResponseStub;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.utils.StubFailMpCall;
import com.mercadopago.android.px.utils.StubSuccessMpCall;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutPresenterTest {

    private static final String DEFAULT_CARD_ID = "260077840";
    private static final String DEBIT_CARD_DEBCABAL = "debcabal";
    public static final int CUSTOM_RESULT_CODE = 1;

    @Mock private Checkout.View checkoutView;
    @Mock private PaymentSettingRepository paymentSettingRepository;
    @Mock private UserSelectionRepository userSelectionRepository;
    @Mock private InitRepository initRepository;
    @Mock private PaymentRepository paymentRepository;

    private CheckoutPresenter presenter;

    @Before
    public void setUp() {
        presenter = getPresenter();
    }

    @Test
    public void whenCheckoutInitializedAndPaymentMethodSearchFailsThenShowError() {
        final ApiException apiException = mock(ApiException.class);
        when(initRepository.init()).thenReturn(new StubFailMpCall<>(apiException));

        presenter.initialize();

        verify(checkoutView).showProgress();
        verify(checkoutView).showError(any(MercadoPagoError.class));
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenChoHasPreferenceAndPaymentMethodRetrievedShowOneTap() {
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(InitResponseStub.FULL.get()));

        presenter.initialize();

        verify(initRepository).init();
        verify(checkoutView).showProgress();
        verify(checkoutView).hideProgress();
        verify(checkoutView).showOneTap();
        verifyNoMoreInteractions(checkoutView);
        verifyNoMoreInteractions(initRepository);
    }

    @Test
    public void whenPaymentResultWithCreatedPaymentThenFinishCheckoutWithPaymentResult() {
        final Payment payment = mock(Payment.class);

        when(paymentRepository.getPayment()).thenReturn(payment);

        presenter.onPaymentResultResponse(null);

        verify(checkoutView).finishWithPaymentResult(null, payment);
    }

    @Test
    public void whenPaymentResultWithoutCreatedPaymentThenFinishCheckoutWithoutPaymentResult() {
        presenter.onPaymentResultResponse(null);
        verify(checkoutView).finishWithPaymentResult(null, null);
    }

    @Test
    public void whenErrorShownAndValidIdentificationThenCancelCheckout() {
        final ApiException apiException = mock(ApiException.class);
        final MercadoPagoError mpException = new MercadoPagoError(apiException, "");

        presenter.onErrorCancel(mpException);

        verify(checkoutView).cancelCheckout();
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenCustomPaymentResultResponseHasPaymentThenFinishWithPaymentResult() {
        final Payment payment = mock(Payment.class);
        when(paymentRepository.getPayment()).thenReturn(payment);

        presenter.onPaymentResultResponse(CUSTOM_RESULT_CODE);

        verify(checkoutView).finishWithPaymentResult(CUSTOM_RESULT_CODE, payment);
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenCustomPaymentResultResponseHasNotPaymentThenFinishWithPaymentResult() {
        presenter.onPaymentResultResponse(CUSTOM_RESULT_CODE);

        verify(checkoutView).finishWithPaymentResult(CUSTOM_RESULT_CODE, null);
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenCustomPaymentResultResponseHasBusinessPaymentThenFinishWithPaymentResult() {
        final BusinessPayment payment = mock(BusinessPayment.class);
        when(paymentRepository.getPayment()).thenReturn(payment);

        presenter.onPaymentResultResponse(CUSTOM_RESULT_CODE);

        verify(checkoutView).finishWithPaymentResult(CUSTOM_RESULT_CODE, null);
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenFailureRecoveryNotSetThenShowFailureRecoveryError() {
        presenter.recoverFromFailure();
        verify(checkoutView).showFailureRecoveryError();
        verifyNoMoreInteractions(checkoutView);
    }

// --------- Helper methods ----------- //

    @NonNull
    private CheckoutPresenter getBasePresenter(final Checkout.View view) {

        presenter = new CheckoutPresenter(paymentSettingRepository, userSelectionRepository,
            initRepository, paymentRepository);

        presenter.attachView(view);
        return presenter;
    }

    @NonNull
    private CheckoutPresenter getPresenter() {
        return getBasePresenter(checkoutView);
    }
}