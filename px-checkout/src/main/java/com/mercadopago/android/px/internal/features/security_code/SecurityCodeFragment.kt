package com.mercadopago.android.px.internal.features.security_code

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.meli.android.carddrawer.model.CardDrawerView
import com.mercadolibre.android.andesui.snackbar.action.AndesSnackbarAction
import com.mercadopago.android.px.R
import com.mercadopago.android.px.core.BackHandler
import com.mercadopago.android.px.internal.di.viewModel
import com.mercadopago.android.px.internal.extensions.showSnackBar
import com.mercadopago.android.px.internal.features.pay_button.PayButton
import com.mercadopago.android.px.internal.features.pay_button.PayButtonFragment
import com.mercadopago.android.px.internal.util.ViewUtils
import com.mercadopago.android.px.internal.util.nonNullObserve
import com.mercadopago.android.px.model.PaymentRecovery
import com.mercadopago.android.px.model.internal.PaymentConfiguration
import com.mercadopago.android.px.tracking.internal.model.Reason

internal class SecurityCodeFragment : Fragment(), PayButton.Handler, BackHandler {

    private val securityCodeViewModel: SecurityCodeViewModel by viewModel()

    private lateinit var cardDrawer: CardDrawerView
    private lateinit var cvvEditText: EditText
    private lateinit var cvvTitle: TextView
    private lateinit var cvvSubtitle: TextView
    private lateinit var payButtonFragment: PayButtonFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_security_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity?)?.apply {
            view.findViewById<Toolbar>(R.id.cvv_toolbar)?.also { toolbar ->
                setSupportActionBar(toolbar)
                supportActionBar?.apply {
                    setDisplayShowTitleEnabled(false)
                    setDisplayHomeAsUpEnabled(true)
                    setDisplayShowHomeEnabled(true)
                    setHomeButtonEnabled(true)
                    toolbar.setNavigationOnClickListener { onBackPressed() }
                }
            }
        }

        cardDrawer = view.findViewById(R.id.card_drawer)
        cvvEditText = view.findViewById(R.id.cvv_edit_text)
        cvvTitle = view.findViewById(R.id.cvv_title)
        cvvSubtitle = view.findViewById(R.id.cvv_subtitle)
        ViewUtils.openKeyboard(cvvEditText)

        arguments?.apply {

            check(
                containsKey(EXTRA_PAYMENT_RECOVERY)
                    || containsKey(EXTRA_PAYMENT_CONFIGURATION)
                    && containsKey(EXTRA_REASON)) {
                "PaymentRecovery or PaymentConfiguration and Reason are needed"
            }

            securityCodeViewModel.init(
                getParcelable(EXTRA_PAYMENT_CONFIGURATION)!!,
                getParcelable(EXTRA_PAYMENT_RECOVERY),
                getString(EXTRA_REASON)?.let { Reason.valueOf(it) })

        } ?: error("Arguments not be null")

        payButtonFragment = childFragmentManager.findFragmentById(R.id.pay_button) as PayButtonFragment
        observeViewModel()
    }

    private fun observeViewModel() {
        with(securityCodeViewModel) {
            cvvCardUiLiveData.nonNullObserve(viewLifecycleOwner) {
                with(cardDrawer) {
                    card.name = it.name
                    card.expiration = it.date
                    card.number = it.number
                    show(it)
                }
            }

            virtualCardInfoLiveData.nonNullObserve(viewLifecycleOwner) {
                cardDrawer.visibility = GONE
                cvvTitle.text = it.title
                with(cvvSubtitle) {
                    text = it.message
                    visibility = VISIBLE
                }
            }

            tokenizeErrorApiLiveData.nonNullObserve(viewLifecycleOwner) {
                val action = AndesSnackbarAction(
                    getString(R.string.px_snackbar_error_action), View.OnClickListener {
                    activity?.onBackPressed()
                })
                view.showSnackBar(getString(R.string.px_error_title), andesSnackbarAction = action)
            }

            inputInfoLiveData.nonNullObserve(viewLifecycleOwner) {
                cvvEditText.filters = arrayOf<InputFilter>(LengthFilter(it))
            }
        }
    }

    override fun prePayment(callback: PayButton.OnReadyForPaymentCallback) {
        securityCodeViewModel.handlePrepayment(callback)
    }

    override fun enqueueOnExploding(callback: PayButton.OnEnqueueResolvedCallback) {
        securityCodeViewModel.enqueueOnExploding(cvvEditText.text.toString(), callback)
    }

    override fun handleBack() = payButtonFragment.isExploding()

    companion object {
        const val TAG = "security_code"
        private const val EXTRA_PAYMENT_CONFIGURATION = "payment_configuration"
        private const val EXTRA_REASON = "reason"
        private const val EXTRA_PAYMENT_RECOVERY = "payment_recovery"

        @JvmStatic
        fun newInstance(paymentConfiguration: PaymentConfiguration, paymentRecovery: PaymentRecovery) =
            SecurityCodeFragment().also {
                it.arguments = Bundle().apply {
                    putParcelable(EXTRA_PAYMENT_CONFIGURATION, paymentConfiguration)
                    putParcelable(EXTRA_PAYMENT_RECOVERY, paymentRecovery)
                }
            }

        @JvmStatic
        fun newInstance(paymentConfiguration: PaymentConfiguration, reason: Reason) =
            SecurityCodeFragment().also {
                it.arguments = Bundle().apply {
                    putParcelable(EXTRA_PAYMENT_CONFIGURATION, paymentConfiguration)
                    putString(EXTRA_REASON, reason.name)
                }
            }
    }
}