package com.mercadopago.android.px.internal.di

import android.content.Context
import com.mercadopago.android.px.internal.viewmodel.drawables.PaymentMethodDrawableItemMapper
import com.mercadopago.android.px.internal.viewmodel.mappers.PaymentMethodDescriptorMapper

internal object MapperProvider {
    fun getPaymentMethodDrawableItemMapper(context: Context): PaymentMethodDrawableItemMapper {
        return PaymentMethodDrawableItemMapper(
            Session.getInstance().configurationModule.chargeRepository,
            Session.getInstance().configurationModule.disabledPaymentMethodRepository, context
        )
    }

    fun getPaymentMethodDrawableItemMapper(): PaymentMethodDrawableItemMapper {
        return PaymentMethodDrawableItemMapper(Session.getInstance().configurationModule.chargeRepository)
    }

    fun getPaymentMethodDescriptorMapper(): PaymentMethodDescriptorMapper {
        return PaymentMethodDescriptorMapper(
            Session.getInstance().configurationModule.paymentSettings,
            Session.getInstance().amountConfigurationRepository,
            Session.getInstance().configurationModule.disabledPaymentMethodRepository,
            Session.getInstance().amountRepository
        )
    }
}