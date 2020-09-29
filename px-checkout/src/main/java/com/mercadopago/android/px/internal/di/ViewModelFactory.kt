package com.mercadopago.android.px.internal.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mercadopago.android.px.internal.core.ConnectionHelper
import com.mercadopago.android.px.internal.features.express.offline_methods.OfflineMethodsViewModel
import com.mercadopago.android.px.internal.features.pay_button.PayButtonViewModel
import com.mercadopago.android.px.internal.features.security_code.SecurityCodeViewModel
import com.mercadopago.android.px.internal.features.security_code.domain.use_case.DisplayDataUseCase
import com.mercadopago.android.px.internal.base.use_case.TokenizeUseCase
import com.mercadopago.android.px.internal.features.security_code.domain.use_case.SecurityTrackModelUseCase
import com.mercadopago.android.px.internal.features.security_code.mapper.BusinessSecurityCodeDisplayDataMapper
import com.mercadopago.android.px.internal.viewmodel.mappers.CardUiMapper
import com.mercadopago.android.px.internal.viewmodel.mappers.PayButtonViewModelMapper

internal class ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val session = Session.getInstance()
        val configurationModule = session.configurationModule
        val paymentSetting = configurationModule.paymentSettings
        val userSelectionRepository = configurationModule.userSelectionRepository

        return when {
            modelClass.isAssignableFrom(PayButtonViewModel::class.java) -> {
                PayButtonViewModel(session.paymentRepository,
                    configurationModule.productIdProvider,
                    ConnectionHelper.instance,
                    paymentSetting,
                    configurationModule.customTextsRepository,
                    PayButtonViewModelMapper())
            }
            modelClass.isAssignableFrom(OfflineMethodsViewModel::class.java) -> {
                OfflineMethodsViewModel(session.initRepository,
                    paymentSetting,
                    session.amountRepository,
                    session.discountRepository)
            }
            modelClass.isAssignableFrom(SecurityCodeViewModel::class.java) -> {
                val tokenizeUseCase = TokenizeUseCase(
                    session.cardTokenRepository,
                    session.mercadoPagoESC,
                    userSelectionRepository)

                val displayDataUseCase = DisplayDataUseCase(
                    session.initRepository,
                    userSelectionRepository,
                    BusinessSecurityCodeDisplayDataMapper())

                SecurityCodeViewModel(
                    configurationModule.paymentSettings,
                    tokenizeUseCase,
                    displayDataUseCase,
                    SecurityTrackModelUseCase(userSelectionRepository),
                    CardUiMapper())
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        } as T
    }
}