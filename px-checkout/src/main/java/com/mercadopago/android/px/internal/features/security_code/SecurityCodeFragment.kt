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
import com.mercadopago.android.px.R
import com.mercadopago.android.px.core.BackHandler
import com.mercadopago.android.px.internal.di.viewModel
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

        (activity as AppCompatActivity?)?.apply {
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
                containsKey(PAYMENT_RECOVERY_EXTRA) || containsKey(PAYMENT_CONFIGURATION_EXTRA) && containsKey(REASON_EXTRA))

            securityCodeViewModel.init(
                getParcelable(PAYMENT_CONFIGURATION_EXTRA)!!,
                getParcelable(PAYMENT_RECOVERY_EXTRA),
                getString(REASON_EXTRA)?.let { Reason.valueOf(it) })

        } ?: error("")

        payButtonFragment = childFragmentManager.findFragmentById(R.id.pay_button) as PayButtonFragment
    }

    override fun onResume() {
        super.onResume()
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

            inputInfoLiveData.nonNullObserve(viewLifecycleOwner) {
                cvvEditText.filters = arrayOf<InputFilter>(LengthFilter(it))
            }
        }
    }

    override fun prePayment(callback: PayButton.OnReadyForPaymentCallback) {
        securityCodeViewModel.handlePrePaymentFinished(callback)
    }

    override fun enqueueOnExploding(callback: PayButton.OnEnqueueResolvedCallback) {
        securityCodeViewModel.enqueueOnExploding(cvvEditText.text.toString(), callback)
    }

    companion object {
        const val TAG = "security_code"
        private const val PAYMENT_CONFIGURATION_EXTRA = "payment_configuration"
        private const val REASON_EXTRA = "reason"
        private const val PAYMENT_RECOVERY_EXTRA = "payment_recovery"

        @JvmStatic
        fun newInstance(paymentConfiguration: PaymentConfiguration, paymentRecovery: PaymentRecovery) = SecurityCodeFragment().also {
            it.arguments = Bundle().apply {
                putParcelable(PAYMENT_CONFIGURATION_EXTRA, paymentConfiguration)
                putParcelable(PAYMENT_RECOVERY_EXTRA, paymentRecovery)
            }
        }

        @JvmStatic
        fun newInstance(paymentConfiguration: PaymentConfiguration, reason: Reason) = SecurityCodeFragment().also {
            it.arguments = Bundle().apply {
                putParcelable(PAYMENT_CONFIGURATION_EXTRA, paymentConfiguration)
                putString(REASON_EXTRA, reason.name)
            }
        }
    }

    override fun handleBack(): Boolean {
        return payButtonFragment.isExploding()
    }
}