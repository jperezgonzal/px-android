package com.mercadopago.android.px.internal.features.pay_button

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mercadolibre.android.andesui.snackbar.action.AndesSnackbarAction
import com.mercadolibre.android.andesui.snackbar.duration.AndesSnackbarDuration
import com.mercadolibre.android.andesui.snackbar.type.AndesSnackbarType
import com.mercadolibre.android.ui.widgets.MeliButton
import com.mercadopago.android.px.R
import com.mercadopago.android.px.addons.BehaviourProvider
import com.mercadopago.android.px.addons.internal.SecurityValidationHandler
import com.mercadopago.android.px.addons.model.SecurityValidationData
import com.mercadopago.android.px.internal.di.viewModel
import com.mercadopago.android.px.internal.extensions.runIfNull
import com.mercadopago.android.px.internal.extensions.showSnackBar
import com.mercadopago.android.px.internal.features.Constants
import com.mercadopago.android.px.internal.features.business_result.BusinessPaymentResultActivity
import com.mercadopago.android.px.internal.features.dummy_result.DummyResultActivity
import com.mercadopago.android.px.internal.features.explode.ExplodeDecorator
import com.mercadopago.android.px.internal.features.explode.ExplodingFragment
import com.mercadopago.android.px.internal.features.payment_result.PaymentResultActivity
import com.mercadopago.android.px.internal.features.plugins.PaymentProcessorActivity
import com.mercadopago.android.px.internal.features.security_code.SecurityCodeFragment
import com.mercadopago.android.px.internal.util.FragmentUtil
import com.mercadopago.android.px.internal.util.ViewUtils
import com.mercadopago.android.px.internal.view.OnSingleClickListener
import com.mercadopago.android.px.internal.viewmodel.PostPaymentAction
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker
import com.mercadopago.android.px.internal.viewmodel.PayButtonViewModel as ButtonConfig

class PayButtonFragment : Fragment(), PayButton.View, SecurityValidationHandler {

    private var buttonStatus = MeliButton.State.NORMAL
    private lateinit var button: MeliButton
    private var retryCounter = 0
    private val viewModel: PayButtonViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pay_button, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when {
            targetFragment is PayButton.Handler -> viewModel.attach(targetFragment as PayButton.Handler)
            parentFragment is PayButton.Handler -> viewModel.attach(parentFragment as PayButton.Handler)
            context is PayButton.Handler -> viewModel.attach(context as PayButton.Handler)
            else -> throw IllegalStateException("Parent should implement ${PayButton.Handler::class.java.simpleName}")
        }

        button = view.findViewById(R.id.confirm_button)
        button.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                viewModel.preparePayment()
            }
        })
        savedInstanceState?.let {
            buttonStatus = it.getInt(EXTRA_STATE, MeliButton.State.NORMAL)
            button.visibility = it.getInt(EXTRA_VISIBILITY, VISIBLE)
            viewModel.recoverFromBundle(it)
            retryCounter = it.getInt(RETRY_COUNTER, 0)
        }
        updateButtonState()

        with(viewModel) {
            buttonTextLiveData.observe(viewLifecycleOwner,
                Observer { buttonConfig -> button.text = buttonConfig!!.getButtonText(this@PayButtonFragment.context!!) })
            cvvRequiredLiveData.observe(viewLifecycleOwner,
                Observer { pair -> pair?.let { showSecurityCodeScreen(SecurityCodeFragment.newInstance(it.first, it.second)) } })
            recoverRequiredLiveData.observe(viewLifecycleOwner,
                Observer { pair -> pair?.let { showSecurityCodeScreen(SecurityCodeFragment.newInstance(it.first, it.second)) } })
            stateUILiveData.observe(viewLifecycleOwner, Observer { state -> state?.let { onStateUIChanged(it) } })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(EXTRA_STATE, buttonStatus)
        outState.putInt(EXTRA_VISIBILITY, button.visibility)
        outState.putInt(RETRY_COUNTER, retryCounter)
        viewModel.storeInBundle(outState)
    }

    private fun onStateUIChanged(stateUI: PayButtonState) {
        when (stateUI) {
            is UIProgress.FingerprintRequired -> startBiometricsValidation(stateUI.validationData)
            is UIProgress.ButtonLoadingStarted -> startLoadingButton(stateUI.timeOut, stateUI.buttonConfig)
            is UIProgress.ButtonLoadingFinished -> finishLoading(stateUI.explodeDecorator)
            is UIProgress.ButtonLoadingCanceled -> cancelLoading()
            is UIResult.VisualProcessorResult -> PaymentProcessorActivity.start(this, REQ_CODE_PAYMENT_PROCESSOR)
            is UIError -> resolveError(stateUI)
            is UIResult.PaymentResult -> PaymentResultActivity.start(this, REQ_CODE_CONGRATS, stateUI.model)
            is UIResult.BusinessPaymentResult ->
                BusinessPaymentResultActivity.start(this, REQ_CODE_CONGRATS, stateUI.model)
            is UIResult.NoCongratsResult -> DummyResultActivity.start(this, REQ_CODE_CONGRATS, stateUI.model)
        }
    }

    override fun stimulate() {
        viewModel.preparePayment()
    }

    override fun enable() {
        buttonStatus = MeliButton.State.NORMAL
        updateButtonState()
    }

    override fun disable() {
        buttonStatus = MeliButton.State.DISABLED
        updateButtonState()
    }

    private fun updateButtonState() {
        if (::button.isInitialized) {
            button.state = buttonStatus
        }
    }

    private fun startBiometricsValidation(validationData: SecurityValidationData) {
        disable()
        BehaviourProvider.getSecurityBehaviour().startValidation(this, validationData, REQ_CODE_BIOMETRICS)
    }

    private fun resolveConnectionError(uiError: UIError.ConnectionError) {
        var action: AndesSnackbarAction? = null
        var type = AndesSnackbarType.NEUTRAL
        var duration = AndesSnackbarDuration.SHORT

        uiError.actionMessage?.also {
            action = AndesSnackbarAction(getString(it), View.OnClickListener { activity?.onBackPressed() })
            type = AndesSnackbarType.ERROR
            duration = AndesSnackbarDuration.LONG
        }

        view.showSnackBar(getString(uiError.message), type, duration, action)
    }

    private fun resolveError(uiError: UIError) {
        when (uiError) {
            is UIError.ConnectionError -> resolveConnectionError(uiError)
            else -> {
                val action = AndesSnackbarAction(
                    getString(R.string.px_snackbar_error_action), View.OnClickListener {
                    activity?.onBackPressed()
                })
                view.showSnackBar(getString(R.string.px_error_title), andesSnackbarAction = action)
            }
        }
    }

    override fun onAnimationFinished() {
        viewModel.hasFinishPaymentAnimation()
    }

    override fun onSecurityValidated(isSuccess: Boolean, securityValidated: Boolean) {
        viewModel.handleBiometricsResult(isSuccess, securityValidated)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQ_CODE_BIOMETRICS) {
            val securityRequested = data?.getBooleanExtra(
                BehaviourProvider.getSecurityBehaviour().extraResultKey, false) ?: false
            enable()
            onSecurityValidated(resultCode == Activity.RESULT_OK, securityRequested)
        } else if (requestCode == REQ_CODE_CONGRATS && resultCode == Constants.RESULT_ACTION) {
            handleAction(data)
        } else if (resultCode == Constants.RESULT_PAYMENT) {
            viewModel.onPostPayment(PaymentProcessorActivity.getPaymentModel(data))
        } else if (resultCode == Constants.RESULT_FAIL_ESC) {
            viewModel.onRecoverPaymentEscInvalid(PaymentProcessorActivity.getPaymentRecovery(data)!!)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleAction(data: Intent?) {
        data?.extras?.let { viewModel.onPostPaymentAction(PostPaymentAction.fromBundle(it)) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.detach()
    }

    override fun onDestroy() {
        FragmentUtil.tryRemoveNow(childFragmentManager, ExplodingFragment.TAG)
        super.onDestroy()
    }

    private fun finishLoading(params: ExplodeDecorator) {
        ViewUtils.hideKeyboard(activity)
        childFragmentManager.findFragmentByTag(ExplodingFragment.TAG)
            ?.let { (it as ExplodingFragment).finishLoading(params) }
            ?: viewModel.hasFinishPaymentAnimation()
    }

    private fun startLoadingButton(paymentTimeout: Int, buttonConfig: ButtonConfig) {
        context?.let {
            button.post {
                if (!isAdded) {
                    FrictionEventTracker.with("/px_checkout/pay_button_loading", FrictionEventTracker.Id.GENERIC,
                        FrictionEventTracker.Style.SCREEN, emptyMap<String, String>())
                } else {
                    val explodingFragment = ExplodingFragment.newInstance(
                        buttonConfig.getButtonProgressText(it), paymentTimeout)
                    childFragmentManager.beginTransaction()
                        .add(R.id.exploding_frame, explodingFragment, ExplodingFragment.TAG)
                        .commitNowAllowingStateLoss()
                    hideConfirmButton()
                }
            }
        }
    }

    private fun cancelLoading() {
        showConfirmButton()
        val fragment = childFragmentManager.findFragmentByTag(ExplodingFragment.TAG) as ExplodingFragment?
        if (fragment != null && fragment.isAdded && fragment.hasFinished()) {
            childFragmentManager
                .beginTransaction()
                .remove(fragment)
                .commitNowAllowingStateLoss()
            restoreStatusBar()
        }
        enable()
    }

    private fun restoreStatusBar() {
        activity?.let { ViewUtils.setStatusBarColor(ContextCompat.getColor(it, R.color.px_colorPrimaryDark), it.window) }
    }

    private fun hideConfirmButton() {
        with(button) {
            clearAnimation()
            visibility = INVISIBLE
        }
    }

    private fun showConfirmButton() {
        with(button) {
            clearAnimation()
            visibility = VISIBLE
        }
    }

    private fun showSecurityCodeScreen(securityCodeFragment: SecurityCodeFragment) {
        activity?.supportFragmentManager?.apply {
            findFragmentByTag(SecurityCodeFragment.TAG).runIfNull {
                beginTransaction()
                    .replace(R.id.one_tap_fragment, securityCodeFragment, SecurityCodeFragment.TAG)
                    .addToBackStack(SecurityCodeFragment.TAG)
                    .commitAllowingStateLoss()
            }
        }
    }

    override fun isExploding(): Boolean {
        return FragmentUtil.isFragmentVisible(childFragmentManager, ExplodingFragment.TAG)
    }

    override fun getParentView() = button

    companion object {
        const val TAG = "TAG_BUTTON_FRAGMENT"
        private const val MAXIMUM_RETRIES = 3
        private const val RETRY_COUNTER = "retry_counter"
        const val REQ_CODE_CONGRATS = 300
        private const val REQ_CODE_PAYMENT_PROCESSOR = 302
        private const val REQ_CODE_BIOMETRICS = 303
        private const val EXTRA_STATE = "extra_state"
        private const val EXTRA_VISIBILITY = "extra_visibility"
    }
}