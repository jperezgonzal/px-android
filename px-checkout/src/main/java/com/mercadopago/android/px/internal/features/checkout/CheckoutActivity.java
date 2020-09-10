package com.mercadopago.android.px.internal.features.checkout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.mercadolibre.android.cardform.internal.CardFormWithFragment;
import com.mercadolibre.android.cardform.internal.LifecycleListener;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.core.BackHandler;
import com.mercadopago.android.px.internal.base.PXActivity;
import com.mercadopago.android.px.internal.di.CheckoutConfigurationModule;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.cardvault.CardVaultActivity;
import com.mercadopago.android.px.internal.features.express.ExpressPayment;
import com.mercadopago.android.px.internal.features.express.ExpressPaymentFragment;
import com.mercadopago.android.px.internal.features.payment_vault.PaymentVaultActivity;
import com.mercadopago.android.px.internal.features.plugins.PaymentProcessorActivity;
import com.mercadopago.android.px.internal.features.review_and_confirm.ReviewAndConfirmBuilder;
import com.mercadopago.android.px.internal.features.security_code.SecurityCodeFragment;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.internal.util.FragmentUtil;
import com.mercadopago.android.px.internal.util.MercadoPagoUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.viewmodel.CheckoutStateModel;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.tracking.internal.events.SessionFrictionEventTracker;

import static com.mercadopago.android.px.core.MercadoPagoCheckout.EXTRA_ERROR;
import static com.mercadopago.android.px.core.MercadoPagoCheckout.EXTRA_PAYMENT_RESULT;
import static com.mercadopago.android.px.core.MercadoPagoCheckout.PAYMENT_RESULT_CODE;
import static com.mercadopago.android.px.internal.features.Constants.RESULT_CANCELED_RYC;
import static com.mercadopago.android.px.internal.features.Constants.RESULT_CANCEL_PAYMENT;
import static com.mercadopago.android.px.internal.features.Constants.RESULT_CHANGE_PAYMENT_METHOD;
import static com.mercadopago.android.px.internal.features.Constants.RESULT_CUSTOM_EXIT;
import static com.mercadopago.android.px.internal.features.Constants.RESULT_SILENT_ERROR;
import static com.mercadopago.android.px.internal.features.payment_result.PaymentResultActivity.EXTRA_RESULT_CODE;
import static com.mercadopago.android.px.internal.util.ErrorUtil.isErrorResult;
import static com.mercadopago.android.px.model.ExitAction.EXTRA_CLIENT_RES_CODE;

public class CheckoutActivity extends PXActivity<CheckoutPresenter>
    implements Checkout.View, ExpressPaymentFragment.CallBack, LifecycleListener {

    private static final String EXTRA_PAYMENT_METHOD_CHANGED = "paymentMethodChanged";
    private static final String EXTRA_PRIVATE_KEY = "extra_private_key";
    private static final String EXTRA_PUBLIC_KEY = "extra_public_key";

    private static final int REQ_PAYMENT_PROCESSOR = 0x03;
    private static final int REQ_CARD_VAULT = 0x04;
    private static final int REQ_REVIEW_AND_CONFIRM = 0x05;
    public static final int REQ_PAYMENT_VAULT = 0x06;
    private static final String TAG_ONETAP_FRAGMENT = "TAG_ONETAP";

    //TODO do not make it public - Needed refactor one tap for this.
    public CheckoutPresenter presenter;

    private String merchantPublicKey;
    @Nullable private String privateKey;
    private Intent customDataBundle;

    public static Intent getIntent(@NonNull final Context context) {
        return new Intent(context, CheckoutActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    @Override
    protected void onHalted() {
        SessionFrictionEventTracker.INSTANCE.track();
    }

    @Override
    protected void onCreated(@Nullable final Bundle savedInstanceState) {
        setContentView(R.layout.px_activity_checkout);
        if (savedInstanceState == null) {
            initPresenter();
        }
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        if (intent.getData() != null) {
            final ExpressPayment.View fragment =
                (ExpressPayment.View) getSupportFragmentManager().findFragmentByTag(TAG_ONETAP_FRAGMENT);
            if (fragment != null) {
                fragment.onDeepLinkReceived();
            }
        } else {
            FragmentUtil.tryRemoveNow(getSupportFragmentManager(), TAG_ONETAP_FRAGMENT);
            // if onNewIntent is called, means that we are initialized twice, so we need to detach previews presenter
            if (presenter != null) {
                presenter.detachView();
            }
            initPresenter();
        }
    }

    private void initPresenter() {
        presenter = getActivityParameters();
        presenter.attachView(this);
        presenter.initialize();
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putString(EXTRA_PRIVATE_KEY, privateKey);
        outState.putString(EXTRA_PUBLIC_KEY, merchantPublicKey);
        if (presenter != null) {
            final CheckoutStateModel state = presenter.getState();
            state.toBundle(outState);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull final Bundle savedInstanceState) {
        final Session session = Session.getInstance();
        final CheckoutConfigurationModule configurationModule = session.getConfigurationModule();

        presenter =
            new CheckoutPresenter(CheckoutStateModel.fromBundle(savedInstanceState),
                configurationModule.getPaymentSettings(),
                configurationModule.getUserSelectionRepository(),
                session.getInitRepository(),
                session.getPluginRepository(),
                session.getPaymentRepository(),
                session.getCongratsRepository(),
                session.getInternalConfiguration());

        privateKey = savedInstanceState.getString(EXTRA_PRIVATE_KEY);
        merchantPublicKey = savedInstanceState.getString(EXTRA_PUBLIC_KEY);
        presenter.attachView(this);

        if (presenter.getState().isExpressCheckout) {
            presenter.initialize();
        }
    }

    @Override
    public void onBackPressed() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            final int backStackEntryCount = fragmentManager.getBackStackEntryCount();

            final Fragment fragment = fragmentManager.findFragmentByTag(CardFormWithFragment.TAG);
            if (fragment != null && fragment.getChildFragmentManager().getBackStackEntryCount() > 0) {
                fragment.getChildFragmentManager().popBackStack();
                return;
            }

            if (earlyExitFromBackHandler(fragmentManager.findFragmentByTag(SecurityCodeFragment.TAG))) {
                return;
            }

            if(earlyExitFromBackHandler(fragmentManager.findFragmentByTag(TAG_ONETAP_FRAGMENT))) {
                return;
            }

            if (backStackEntryCount > 0) {
                fragmentManager.popBackStack();
                return;
            }

            super.onBackPressed();
        }
    }

    private boolean earlyExitFromBackHandler(@Nullable final Fragment fragment) {
        if (fragment instanceof BackHandler && fragment.isAdded()) {
            return ((BackHandler) fragment).handleBack();
        }
        return false;
    }

    protected CheckoutPresenter getActivityParameters() {
        final Session session = Session.getInstance();
        final CheckoutConfigurationModule configurationModule = session.getConfigurationModule();
        final PaymentSettingRepository configuration = configurationModule.getPaymentSettings();

        privateKey = configuration.getPrivateKey();

        final CheckoutStateModel persistentData = new CheckoutStateModel();

        merchantPublicKey = configuration.getPublicKey();

        return new CheckoutPresenter(persistentData,
            configuration,
            configurationModule.getUserSelectionRepository(),
            session.getInitRepository(),
            session.getPluginRepository(),
            session.getPaymentRepository(),
            session.getCongratsRepository(),
            session.getInternalConfiguration());
    }

    @Override
    @SuppressLint("SourceLockedOrientationActivity")
    public void showOneTap() {
        //One tap only supports portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        final FragmentManager supportFragmentManager = getSupportFragmentManager();

        if (supportFragmentManager.findFragmentByTag(TAG_ONETAP_FRAGMENT) == null) {
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.px_slide_right_to_left_in, R.anim.px_slide_right_to_left_out)
                .replace(R.id.one_tap_fragment, ExpressPaymentFragment.getInstance(), TAG_ONETAP_FRAGMENT)
                .commitNowAllowingStateLoss();
        }
    }

    @Override
    public void hideProgress() {
        ViewUtils.showRegularLayout(this);
    }

    @Override
    public void showFailureRecoveryError() {
        showError(MercadoPagoError.createNotRecoverable(getString(R.string.px_error_failure_recovery_not_defined)));
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case ErrorUtil.ERROR_REQUEST_CODE:
            resolveErrorRequest(resultCode, data);
            break;
        case REQ_PAYMENT_VAULT:
            resolvePaymentVaultRequest(resultCode, data);
            break;
        case REQ_CARD_VAULT:
            resolveCardVaultRequest(resultCode, data);
            break;
        default:
            resolveCodes(resultCode, data);
            break;
        }
    }

    public void resolveCodes(final int resultCode, final Intent data) {
        // TODO check cancel on payment processor.
        switch (resultCode) {
        case RESULT_CHANGE_PAYMENT_METHOD:
            presenter.onChangePaymentMethod();
            break;
        case RESULT_CANCEL_PAYMENT:
            resolveCancelReviewAndConfirm(data);
            break;
        case RESULT_CANCELED:
            handleCancel();
            break;
        case RESULT_CANCELED_RYC:
            presenter.onReviewAndConfirmCancel();
            break;
        case RESULT_CUSTOM_EXIT:
            handleCustomExit(data);
            break;
        default:
            break;
        }
    }

    private void handleCancel() {
        presenter.cancelCheckout();
    }

    private void handleCustomExit(final Intent data) {
        if (data != null && data.hasExtra(EXTRA_CLIENT_RES_CODE)) {
            //Business custom exit
            final int resCode = data.getIntExtra(EXTRA_CLIENT_RES_CODE, RESULT_OK);
            presenter.onPaymentResultResponse(resCode);
        } else if (data != null && data.hasExtra(EXTRA_RESULT_CODE)) {
            //Custom exit  - Result screen.
            final Integer finalResultCode = data.getIntExtra(EXTRA_RESULT_CODE, PAYMENT_RESULT_CODE);
            customDataBundle = data;
            presenter.onPaymentResultResponse(finalResultCode);
        } else {
            //Normal exit - Result screen.
            presenter.onPaymentResultResponse(null);
        }
    }

    @Override
    public void transitionOut() {
        overrideTransitionOut();
    }

    private void resolveCancelReviewAndConfirm(final Intent data) {
        if (data != null && data.hasExtra(EXTRA_CLIENT_RES_CODE)) {
            final Integer customResultCode = data.getIntExtra(EXTRA_CLIENT_RES_CODE, 0);
            presenter.onCustomReviewAndConfirmResponse(customResultCode);
        } else {
            MercadoPagoError mercadoPagoError = null;
            if (isErrorResult(data)) {
                mercadoPagoError = (MercadoPagoError) data.getSerializableExtra(EXTRA_ERROR);
            }
            if (mercadoPagoError == null) {
                presenter.cancelCheckout();
            } else {
                presenter.onReviewAndConfirmError(mercadoPagoError);
            }
        }
    }

    protected void resolveCardVaultRequest(final int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            presenter.onCardFlowResponse();
        } else {
            //TODO CHECK WHEN IT HAPPENS.
            final MercadoPagoError mercadoPagoError =
                isErrorResult(data) ?
                    (MercadoPagoError) data.getSerializableExtra(EXTRA_ERROR) : null;
            if (mercadoPagoError == null) {
                presenter.onCardFlowCancel();
            } else {
                presenter.onTerminalError(mercadoPagoError);
            }
        }
    }

    private void resolvePaymentVaultRequest(final int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            presenter.onPaymentMethodSelected();
        } else if (resultCode == RESULT_SILENT_ERROR) {
            cancelCheckout();
        } else if (isErrorResult(data)) {
            //TODO check when it happens.
            final MercadoPagoError mercadoPagoError = (MercadoPagoError) data.getSerializableExtra(EXTRA_ERROR);
            presenter.onPaymentMethodSelectionError(mercadoPagoError);
        } else {
            presenter.onPaymentMethodSelectionCancel();
        }
    }

    @Override
    public void showReviewAndConfirm(final boolean isUniquePaymentMethod) {
        overrideTransitionOut();
        overrideTransitionIn();
        final Intent intent = new ReviewAndConfirmBuilder()
            .setHasExtraPaymentMethods(!isUniquePaymentMethod)
            .getIntent(this);
        startActivityForResult(intent, REQ_REVIEW_AND_CONFIRM);
    }

    @Override
    public void showPaymentMethodSelection() {
        if (isActive()) {
            PaymentVaultActivity.start(this, REQ_PAYMENT_VAULT);
        }
    }

    @Override
    public void showSavedCardFlow(final Card card) {
        CardVaultActivity.startActivity(this, REQ_CARD_VAULT);
    }

    @Override
    public void showNewCardFlow() {
        CardVaultActivity.startActivity(this, REQ_CARD_VAULT);
    }

    private void resolveErrorRequest(final int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            presenter.recoverFromFailure();
        } else {
            final MercadoPagoError mercadoPagoError =
                isErrorResult(data) ? (MercadoPagoError) data.getSerializableExtra(EXTRA_ERROR) : null;
            presenter.onErrorCancel(mercadoPagoError);
        }
    }

    @Override
    public void showError(final MercadoPagoError error) {
        ErrorUtil.startErrorActivity(this, error);
    }

    @Override
    public void showProgress() {
        ViewUtils.showProgressLayout(this);
    }

    @Override
    public void showPaymentProcessorWithAnimation() {
        overrideTransitionOut();
        overrideTransitionIn();
        PaymentProcessorActivity.start(this, REQ_PAYMENT_PROCESSOR);
    }

    @Override
    public boolean isActive() {
        return !isFinishing();
    }

    @Override
    public void onOneTapCanceled() {
        cancelCheckout();
    }

    @Override
    public void cancelCheckout(final Integer resultCode, final Boolean paymentMethodEdited) {
        final Intent intent = new Intent();
        intent.putExtra(EXTRA_PAYMENT_METHOD_CHANGED, paymentMethodEdited);
        setResult(resultCode, intent);
        finish();
        overrideTransitionOut();
    }

    @Override
    public void goToLink(@NonNull final String link) {
        final Intent intent = MercadoPagoUtil.getIntent(this, link);
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public void openInWebView(@NonNull final String link) {
        final Intent intent = MercadoPagoUtil.getNativeOrWebViewIntent(this, link);
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public void finishWithPaymentResult(@Nullable final Integer resultCode, @Nullable final Payment payment) {
        int defaultResultCode = RESULT_OK;
        final Intent intent = new Intent();
        if (customDataBundle != null) {
            intent.putExtras(customDataBundle);
        }
        if (payment != null) {
            defaultResultCode = PAYMENT_RESULT_CODE;
            intent.putExtra(EXTRA_PAYMENT_RESULT, payment);
        }
        setResult(resultCode != null ? resultCode : defaultResultCode, intent);
        finish();
        overrideTransitionOut();
    }

    @Override
    public void cancelCheckout(final MercadoPagoError mercadoPagoError) {
        final Intent intent = new Intent();
        intent.putExtra(EXTRA_ERROR, mercadoPagoError);
        setResult(RESULT_CANCELED, intent);
        finish();
        overrideTransitionOut();
    }

    @Override
    public void cancelCheckout() {
        setResult(RESULT_CANCELED);
        finish();
        overrideTransitionOut();
    }

    @Override
    public void onCardAdded(@NonNull final String cardId, @NonNull final LifecycleListener.Callback callback) {
        presenter.onCardAdded(cardId, callback);
    }
}