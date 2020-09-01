package com.mercadopago.android.px.internal.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mercadopago.android.px.internal.core.ConnectionHelper
import com.mercadopago.android.px.internal.features.express.offline_methods.OfflineMethodsViewModel
import com.mercadopago.android.px.internal.features.pay_button.PayButtonViewModel
import com.mercadopago.android.px.internal.features.security_code.CardConfigurationMapper
import com.mercadopago.android.px.internal.features.security_code.SecurityCodeViewModel

internal class ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val session = Session.getInstance()
        if (modelClass.isAssignableFrom(PayButtonViewModel::class.java)) {
            return PayButtonViewModel(session.paymentRepository,
                    session.configurationModule.productIdProvider,
                ConnectionHelper.instance,
                    session.configurationModule.paymentSettings,
                    session.configurationModule.customTextsRepository) as T
        } else if(modelClass.isAssignableFrom(OfflineMethodsViewModel::class.java)) {
            return OfflineMethodsViewModel(session.initRepository,
                    session.configurationModule.paymentSettings,
                    session.amountRepository,
                    session.discountRepository) as T
        } else if(modelClass.isAssignableFrom(SecurityCodeViewModel::class.java)) {
            return SecurityCodeViewModel(session.initRepository,
                    session.configurationModule.userSelectionRepository,
                    CardConfigurationMapper()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}