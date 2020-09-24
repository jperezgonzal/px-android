package com.mercadopago.android.px.internal.features.review_and_confirm;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.configuration.AdvancedConfiguration;
import com.mercadopago.android.px.configuration.ReviewAndConfirmConfiguration;
import com.mercadopago.android.px.core.DynamicDialogCreator;
import com.mercadopago.android.px.internal.base.PXActivity;
import com.mercadopago.android.px.internal.di.CheckoutConfigurationModule;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.Constants;
import com.mercadopago.android.px.internal.features.express.RenderMode;
import com.mercadopago.android.px.internal.features.pay_button.PayButton;
import com.mercadopago.android.px.internal.features.pay_button.PayButtonFragment;
import com.mercadopago.android.px.internal.features.review_and_confirm.components.ReviewAndConfirmContainer;
import com.mercadopago.android.px.internal.features.review_and_confirm.components.actions.CancelPaymentAction;
import com.mercadopago.android.px.internal.features.review_and_confirm.components.actions.ChangePaymentMethodAction;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.ItemsModel;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.ReviewAndConfirmViewModel;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.SummaryModel;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.TermsAndConditionsModel;
import com.mercadopago.android.px.internal.font.FontHelper;
import com.mercadopago.android.px.internal.font.PxFont;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.ComponentManager;
import com.mercadopago.android.px.internal.view.LinkableTextComponent;
import com.mercadopago.android.px.internal.viewmodel.PostPaymentAction;
import com.mercadopago.android.px.model.Action;
import com.mercadopago.android.px.model.ExitAction;
import com.mercadopago.android.px.model.Payer;
import com.mercadopago.android.px.model.display_info.LinkableText;
import org.jetbrains.annotations.NotNull;

import static com.mercadopago.android.px.internal.features.Constants.RESULT_CANCELED_RYC;
import static com.mercadopago.android.px.internal.features.Constants.RESULT_CANCEL_PAYMENT;
import static com.mercadopago.android.px.internal.features.Constants.RESULT_CHANGE_PAYMENT_METHOD;

public final class ReviewAndConfirmActivity extends PXActivity<ReviewAndConfirmPresenter> implements
    ReviewAndConfirm.View, ActionDispatcher, PayButton.Handler {

    private static final String EXTRA_TERMS_AND_CONDITIONS = "extra_terms_and_conditions";
    private static final String EXTRA_DISPLAY_INFO_LINKABLE_TEXT =
        "extra_digital_currency_terms_and_conditions";
    private static final String EXTRA_RYC_MODEL = "extra_ryc_model";
    private static final String EXTRA_SUMMARY_MODEL = "extra_summary_model";
    private static final String EXTRA_ITEMS = "extra_items";
    private static final String EXTRA_DISCOUNT_TERMS_AND_CONDITIONS = "extra_discount_terms_and_conditions";
    private static final String TAG_DYNAMIC_DIALOG = "tag_dynamic_dialog";

    private PayButtonFragment payButton;

    public static Intent getIntentForAction(@NonNull final Context context,
        @Nullable final TermsAndConditionsModel mercadoPagoTermsAndConditions,
        @Nullable final LinkableText linkableText,
        @NonNull final ReviewAndConfirmViewModel reviewAndConfirmViewModel,
        @NonNull final SummaryModel summaryModel,
        @NonNull final ItemsModel itemsModel,
        @Nullable final TermsAndConditionsModel discountTermsAndConditions) {

        final Intent intent = new Intent(context, ReviewAndConfirmActivity.class);
        intent.putExtra(EXTRA_TERMS_AND_CONDITIONS, mercadoPagoTermsAndConditions);
        intent.putExtra(EXTRA_DISPLAY_INFO_LINKABLE_TEXT, (Parcelable) linkableText);
        intent.putExtra(EXTRA_RYC_MODEL, reviewAndConfirmViewModel);
        intent.putExtra(EXTRA_SUMMARY_MODEL, summaryModel);
        intent.putExtra(EXTRA_ITEMS, itemsModel);
        intent.putExtra(EXTRA_DISCOUNT_TERMS_AND_CONDITIONS, discountTermsAndConditions);

        return intent;
    }

    @Override
    protected void onCreated(@Nullable final Bundle savedInstanceState) {
        setContentView(R.layout.px_view_container_review_and_confirm);
        payButton = (PayButtonFragment) getSupportFragmentManager().findFragmentById(R.id.pay_button);
        initializeViews();
        final Session session = Session.getInstance();

        presenter = new ReviewAndConfirmPresenter(session.getPaymentRepository(),
            session.getDiscountRepository(),
            session.getConfigurationModule().getPaymentSettings(),
            session.getConfigurationModule().getUserSelectionRepository(),
            session.getMercadoPagoESC());
        presenter.attachView(this);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.RESULT_CUSTOM_EXIT) {
            setResult(resultCode, data);
            finish();
        }
    }

    private void initializeViews() {
        initToolbar();
        initBody();
    }

    private void initBody() {
        final ViewGroup mainContent = findViewById(R.id.scroll_view);
        final ContainerProps props = getActivityParameters();
        initContent(mainContent, props.reviewAndConfirmContainerProps);
        initFloatingButton(mainContent, props.linkableText);
    }

    private void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayShowTitleEnabled(false);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.px_activity_checkout_title));
        FontHelper.setFont(collapsingToolbarLayout, PxFont.REGULAR);
    }

    private void initFloatingButton(final ViewGroup scrollView, @Nullable final LinkableText linkableText) {
        final LinearLayout floatingLayout = findViewById(R.id.floating_layout);
        if (linkableText != null && TextUtil.isNotEmpty(linkableText.getText())) {
            final LinkableTextComponent linkableTextComponent = new LinkableTextComponent(linkableText);
            floatingLayout.addView(linkableTextComponent.render(floatingLayout), 0);
        }

        configureFloatingBehaviour(scrollView, floatingLayout);
    }

    private void configureFloatingBehaviour(final ViewGroup scrollView, final View floatingConfirmLayout) {
        addScrollBottomPadding(floatingConfirmLayout, scrollView);
        configureScrollLayoutListener(floatingConfirmLayout, scrollView);
        addScrollListener(floatingConfirmLayout, scrollView);
    }

    private void addScrollBottomPadding(final View floatingConfirmLayout, final View scrollView) {
        final ViewTreeObserver floatingObserver = floatingConfirmLayout.getViewTreeObserver();
        floatingObserver.addOnGlobalLayoutListener(() -> {
            final int bottomPadding = floatingConfirmLayout.getHeight();
            if (scrollView.getPaddingBottom() != bottomPadding) {
                scrollView.setPadding(scrollView.getPaddingLeft(), scrollView.getPaddingTop(),
                    scrollView.getPaddingRight(), bottomPadding);
            }
        });
    }

    private void configureScrollLayoutListener(final View floatingConfirmLayout, final ViewGroup scrollView) {
        final ViewTreeObserver viewTreeObserver = scrollView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(
            () -> resolveFloatingButtonElevationVisibility(floatingConfirmLayout, scrollView));
    }

    private void addScrollListener(final View floatingConfirmLayout, final ViewGroup scrollView) {
        final ViewTreeObserver viewTreeObserver = scrollView.getViewTreeObserver();
        viewTreeObserver.addOnScrollChangedListener(
            () -> resolveFloatingButtonElevationVisibility(floatingConfirmLayout, scrollView));
    }

    /* default */ void resolveFloatingButtonElevationVisibility(final View floatingConfirmLayout,
        final ViewGroup scrollView) {
        final ViewGroup content = (ViewGroup) scrollView.getChildAt(0);
        final int containerHeight = content.getHeight();
        final float finalSize = containerHeight - scrollView.getHeight();
        setFloatingElevationVisibility(floatingConfirmLayout, scrollView.getScrollY() < finalSize);
    }

    private void initContent(final ViewGroup mainContent, final ReviewAndConfirmContainer.Props props) {
        final ComponentManager manager = new ComponentManager(this);
        final ReviewAndConfirmContainer container = new ReviewAndConfirmContainer(props, this);

        container.setDispatcher(this);

        manager.render(container, mainContent);
    }

    private ContainerProps getActivityParameters() {
        final Intent intent = getIntent();
        final Bundle extras = intent.getExtras();

        if (extras != null) {
            final TermsAndConditionsModel termsAndConditionsModel = extras.getParcelable(EXTRA_TERMS_AND_CONDITIONS);
            final ReviewAndConfirmViewModel reviewAndConfirmViewModel = extras.getParcelable(EXTRA_RYC_MODEL);

            final SummaryModel summaryModel = extras.getParcelable(EXTRA_SUMMARY_MODEL);
            final ItemsModel itemsModel = extras.getParcelable(EXTRA_ITEMS);
            final TermsAndConditionsModel discountTermsAndConditions =
                extras.getParcelable(EXTRA_DISCOUNT_TERMS_AND_CONDITIONS);

            final Session session = Session.getInstance();
            final CheckoutConfigurationModule configurationModule = session.getConfigurationModule();
            final AdvancedConfiguration advancedConfiguration = configurationModule.getPaymentSettings()
                .getAdvancedConfiguration();
            final Payer payer = configurationModule.getPaymentSettings().getCheckoutPreference().getPayer();

            final ReviewAndConfirmConfiguration reviewAndConfirmConfiguration =
                advancedConfiguration.getReviewAndConfirmConfiguration();

            final LinkableText linkableText = extras.getParcelable(EXTRA_DISPLAY_INFO_LINKABLE_TEXT);

            ReviewAndConfirmContainer.Props reviewAndConfirmContainerProps =
                new ReviewAndConfirmContainer.Props(termsAndConditionsModel,
                    reviewAndConfirmViewModel,
                    summaryModel,
                    reviewAndConfirmConfiguration,
                    advancedConfiguration.getDynamicFragmentConfiguration(),
                    itemsModel,
                    discountTermsAndConditions,
                    payer);

            return new ContainerProps(reviewAndConfirmContainerProps, linkableText);
        }

        throw new IllegalStateException("Unsupported parameters for Review and confirm activity");
    }

    private void setFloatingElevationVisibility(final View floatingConfirmLayout, final boolean visible) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final float elevationInPixels =
                visible ? getBaseContext().getResources().getDimension(R.dimen.px_xxs_margin) : 0;
            floatingConfirmLayout.setElevation(elevationInPixels);
        }
    }

    @Override
    public void dispatch(final Action action) {
        if (action instanceof ChangePaymentMethodAction) {
            presenter.onChangePaymentMethod();
        } else if (action instanceof CancelPaymentAction) {
            onBackPressed();
        } else if (action instanceof ExitAction) {
            processCustomExit((ExitAction) action);
        } else {
            throw new UnsupportedOperationException("action not allowed");
        }
    }

    @Override
    public void onBackPressed() {
        if (payButton.isExploding()) {
            return;
        }

        presenter.onBackPressed();
        setResult(RESULT_CANCELED_RYC);
        super.onBackPressed();
    }

    private void processCustomExit(final ExitAction action) {
        setResult(RESULT_CANCEL_PAYMENT, action.toIntent());
        finish();
    }

    @Override
    public void showDynamicDialog(@NonNull final DynamicDialogCreator creator,
        @NonNull final DynamicDialogCreator.CheckoutData checkoutData) {
        if (creator.shouldShowDialog(this, checkoutData)) {
            creator.create(this, checkoutData).show(getSupportFragmentManager(), TAG_DYNAMIC_DIALOG);
        }
    }

    @Override
    public void finishAndChangePaymentMethod() {
        setResult(RESULT_CHANGE_PAYMENT_METHOD);
        finish();
    }

    @Override
    public void prePayment(@NotNull final PayButton.OnReadyForPaymentCallback callback) {
        presenter.onPrePayment(callback);
    }

    @Override
    public void onPostPaymentAction(@NotNull final PostPaymentAction postPaymentAction) {
        presenter.onPostPaymentAction(postPaymentAction);
    }

    @NonNull
    @Override
    public PayButton.CvvRequestedModel onCvvRequested() {
        return new PayButton.CvvRequestedModel(0, RenderMode.NO_CARD);
    }

    public static final class ContainerProps {
        /* default */ ReviewAndConfirmContainer.Props reviewAndConfirmContainerProps;
        /* default */ LinkableText linkableText;

        /* default */ ContainerProps(
            final ReviewAndConfirmContainer.Props reviewAndConfirmContainerProps,
            final LinkableText linkableText) {
            this.reviewAndConfirmContainerProps = reviewAndConfirmContainerProps;
            this.linkableText = linkableText;
        }
    }
}