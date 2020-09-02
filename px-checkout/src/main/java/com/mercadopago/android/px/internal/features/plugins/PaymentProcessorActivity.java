package com.mercadopago.android.px.internal.features.plugins;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.core.PaymentProcessor;
import com.mercadopago.android.px.core.SplitPaymentProcessor;
import com.mercadopago.android.px.internal.base.PXActivity;
import com.mercadopago.android.px.internal.callbacks.PaymentServiceHandler;
import com.mercadopago.android.px.internal.callbacks.PaymentServiceHandlerWrapper;
import com.mercadopago.android.px.internal.di.CheckoutConfigurationModule;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.GenericPayment;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.internal.GenericPaymentDescriptor;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.tracking.internal.model.Reason;
import java.util.List;

import static com.mercadopago.android.px.internal.features.Constants.RESULT_FAIL_ESC;
import static com.mercadopago.android.px.internal.features.Constants.RESULT_PAYMENT;
import static com.mercadopago.android.px.internal.util.ErrorUtil.ERROR_REQUEST_CODE;

public final class PaymentProcessorActivity extends PXActivity
    implements SplitPaymentProcessor.OnPaymentListener,
    PaymentProcessor.OnPaymentListener {

    private static final String TAG_PROCESSOR_FRAGMENT = "TAG_PROCESSOR_FRAGMENT";
    private static final String EXTRA_PAYMENT = "extra_payment";
    private static final String EXTRA_RECOVERY = "extra_recovery";

    private PaymentServiceHandlerWrapper paymentServiceHandlerWrapper;
    private PaymentServiceHandler wrapper;

    public static void start(@NonNull final Activity activity, final int requestCode) {
        activity.startActivityForResult(getIntent(activity), requestCode);
    }

    public static void start(@NonNull final Fragment fragment, final int requestCode) {
        fragment.startActivityForResult(getIntent(fragment.getContext()), requestCode);
    }

    @NonNull
    private static Intent getIntent(final Context context) {
        return new Intent(context, PaymentProcessorActivity.class);
    }

    @NonNull
    public static PaymentModel getPaymentModel(final Intent intent) {
        PaymentModel paymentModel = null;
        if (intent.hasExtra(EXTRA_PAYMENT)) {
            //noinspection ConstantConditions
            paymentModel = (PaymentModel) intent.getExtras().get(EXTRA_PAYMENT);
        }
        if (paymentModel == null) {
            throw new IllegalStateException("No paymentModel passed to process");
        }
        return paymentModel;
    }

    @Nullable
    public static PaymentRecovery getPaymentRecovery(final Intent intent) {
        return (PaymentRecovery) intent.getExtras().get(EXTRA_RECOVERY);
    }

    @Override
    protected void onCreated(@Nullable final Bundle savedInstanceState) {
        final FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(R.id.px_main_container);
        setContentView(frameLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT));

        final Session session = Session.getInstance();

        paymentServiceHandlerWrapper = new PaymentServiceHandlerWrapper(session.getPaymentRepository(),
            session.getConfigurationModule().getDisabledPaymentMethodRepository(),
            session.getEscPaymentManager(), session.getInstructionsRepository(),
            session.getCongratsRepository(), session.getConfigurationModule().getUserSelectionRepository());

        if (getFragmentByTag() == null) { // if fragment is not added, then create it.
            addPaymentProcessorFragment(getSupportFragmentManager(), session);
        }
    }

    @Nullable
    private Fragment getFragmentByTag() {
        return getSupportFragmentManager().findFragmentByTag(TAG_PROCESSOR_FRAGMENT);
    }

    private void addPaymentProcessorFragment(@NonNull final FragmentManager supportFragmentManager,
        @NonNull final Session session) {

        final CheckoutConfigurationModule configurationModule = session.getConfigurationModule();
        final PaymentSettingRepository paymentSettings = configurationModule.getPaymentSettings();

        final SplitPaymentProcessor paymentProcessor = paymentSettings
            .getPaymentConfiguration()
            .getPaymentProcessor();

        final List<PaymentData> paymentData = session
            .getPaymentRepository()
            .getPaymentDataList();

        final CheckoutPreference checkoutPreference = paymentSettings.getCheckoutPreference();
        final String securityType = paymentSettings.getSecurityType().getValue();
        final SplitPaymentProcessor.CheckoutData checkoutData =
            new SplitPaymentProcessor.CheckoutData(paymentData, checkoutPreference, securityType);

        final Fragment fragment = paymentProcessor.getFragment(checkoutData, this);

        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.px_main_container, fragment, TAG_PROCESSOR_FRAGMENT)
                .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        wrapper = createWrapper();
        paymentServiceHandlerWrapper.setHandler(wrapper);
        paymentServiceHandlerWrapper.processMessages();
    }

    @Override
    protected void onPause() {
        paymentServiceHandlerWrapper.detach(wrapper);
        super.onPause();
    }

    @NonNull
    private PaymentServiceHandler createWrapper() {
        return new PaymentServiceHandler() {
            @Override
            public void onCvvRequired(@NonNull final Card card, @NonNull final Reason reason) {
                // do nothing
            }

            @Override
            public void onVisualPayment() {
                // do nothing
            }

            @Override
            public void onRecoverPaymentEscInvalid(final PaymentRecovery recovery) {
                final Intent intent = new Intent();
                intent.putExtra(EXTRA_RECOVERY, (Parcelable) recovery);
                setResult(RESULT_FAIL_ESC, intent);
                finish();
            }

            @Override
            public void onPostPayment(@NonNull final PaymentModel paymentModel) {
                final Intent intent = new Intent();
                intent.putExtra(EXTRA_PAYMENT, paymentModel);
                setResult(RESULT_PAYMENT, intent);
                finish();
            }

            @Override
            public void onPaymentError(@NonNull final MercadoPagoError error) {
                //TODO verify error handling
                ErrorUtil.startErrorActivity(PaymentProcessorActivity.this, error);
            }
        };
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ERROR_REQUEST_CODE) {
            //TODO verify error handling
            finishWithCanceledResult();
        }
    }

    @Override
    public void onPaymentFinished(@NonNull final IPaymentDescriptor payment) {
        paymentServiceHandlerWrapper.onPaymentFinished(payment);
    }

    @Override
    public void onPaymentFinished(@NonNull final Payment payment) {
        paymentServiceHandlerWrapper.onPaymentFinished(payment);
    }

    @Override
    public void onPaymentFinished(@NonNull final GenericPayment genericPayment) {
        paymentServiceHandlerWrapper.onPaymentFinished(GenericPaymentDescriptor.with(genericPayment));
    }

    @Override
    public void onPaymentFinished(@NonNull final BusinessPayment businessPayment) {
        paymentServiceHandlerWrapper.onPaymentFinished(businessPayment);
    }

    @Override
    public void onPaymentError(@NonNull final MercadoPagoError error) {
        paymentServiceHandlerWrapper.onPaymentError(error);
    }

    @Override
    public void onBackPressed() {
        final Fragment fragment = getFragmentByTag();
        if (isBackHandlerFragment(fragment)) {
            if (((SplitPaymentProcessor.BackHandler) fragment).isBackEnabled()) {
                finishWithCanceledResult();
            } else {
                //TODO: maybe can track this scenario
                //Do nothing
            }
        } else {
            finishWithCanceledResult();
        }
    }

    private boolean isBackHandlerFragment(@Nullable final Fragment fragment) {
        return fragment instanceof SplitPaymentProcessor.BackHandler;
    }

    private void finishWithCanceledResult() {
        setResult(RESULT_CANCELED);
        finish();
    }
}