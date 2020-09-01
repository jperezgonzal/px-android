package com.mercadopago.android.px.internal.features.security_code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.meli.android.carddrawer.model.CardDrawerView
import com.mercadopago.android.px.R
import com.mercadopago.android.px.core.BackHandler
import com.mercadopago.android.px.internal.di.viewModel
import com.mercadopago.android.px.internal.features.pay_button.PayButton
import com.mercadopago.android.px.internal.util.nonNullObserve

internal class SecurityCodeFragment : Fragment(), PayButton.Handler, BackHandler {

    private val securityCodeViewModel: SecurityCodeViewModel by viewModel()

    private lateinit var cardDrawer: CardDrawerView

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
        }
    }

    override fun prePayment(callback: PayButton.OnReadyForPaymentCallback) {
        //callback.call()
    }

    companion object {
        const val TAG = "security_code"

        @JvmStatic
        fun newInstance() = SecurityCodeFragment()
    }

    override fun handleBack(): Boolean {
        return false
    }
}