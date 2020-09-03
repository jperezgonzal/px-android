package com.mercadopago.android.px.internal.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mercadopago.android.px.internal.core.ConnectionHelper
import com.mercadopago.android.px.internal.features.express.offline_methods.OfflineMethodsViewModel
import com.mercadopago.android.px.internal.features.pay_button.PayButtonViewModel
import com.mercadopago.android.px.internal.features.security_code.CardConfigurationMapper
import com.mercadopago.android.px.internal.features.security_code.SecurityCodeViewModel
import com.mercadopago.android.px.internal.features.security_code.VirtualCardInfoMapper

internal class ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val session = Session.getInstance()
        val configurationModule = session.configurationModule

        return when {
            modelClass.isAssignableFrom(PayButtonViewModel::class.java) -> {
                PayButtonViewModel(session.paymentRepository,
                    configurationModule.productIdProvider,
                    ConnectionHelper.instance,
                    configurationModule.paymentSettings,
                    configurationModule.customTextsRepository)
            }
            modelClass.isAssignableFrom(OfflineMethodsViewModel::class.java) -> {
                OfflineMethodsViewModel(session.initRepository,
                    configurationModule.paymentSettings,
                    session.amountRepository,
                    session.discountRepository)
            }
            modelClass.isAssignableFrom(SecurityCodeViewModel::class.java) -> {
                SecurityCodeViewModel(
                    session.cardTokenRepository,
                    session.mercadoPagoESC,
                    configurationModule.paymentSettings,
                    session.initRepository,
                    configurationModule.userSelectionRepository,
                    CardConfigurationMapper(),
                    VirtualCardInfoMapper())
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        } as T
    }
}