package com.mercadopago.android.px.internal.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mercadopago.android.px.internal.core.ConnectionHelper
import com.mercadopago.android.px.internal.features.express.offline_methods.OfflineMethodsViewModel
import com.mercadopago.android.px.internal.features.pay_button.PayButtonViewModel
import com.mercadopago.android.px.internal.features.security_code.SecurityCodeViewModel
import com.mercadopago.android.px.internal.features.security_code.use_case.DisplayInfoUseCase
import com.mercadopago.android.px.internal.base.use_case.TokenizeUseCase
import com.mercadopago.android.px.internal.viewmodel.mappers.PayButtonViewModelMapper

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
                    configurationModule.customTextsRepository,
                    PayButtonViewModelMapper())
            }
            modelClass.isAssignableFrom(OfflineMethodsViewModel::class.java) -> {
                OfflineMethodsViewModel(session.initRepository,
                    configurationModule.paymentSettings,
                    session.amountRepository,
                    session.discountRepository)
            }
            modelClass.isAssignableFrom(SecurityCodeViewModel::class.java) -> {
                val tokenizeUseCase = TokenizeUseCase(
                    session.cardTokenRepository,
                    session.mercadoPagoESC,
                    configurationModule.userSelectionRepository)

                SecurityCodeViewModel(
                    configurationModule.paymentSettings,
                    tokenizeUseCase,
                    DisplayInfoUseCase(session.initRepository, configurationModule.userSelectionRepository))
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        } as T
    }
}