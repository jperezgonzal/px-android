package com.mercadopago.android.px.internal.viewmodel.drawables

import android.content.Context
import com.mercadopago.android.px.internal.repository.ChargeRepository
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository
import com.mercadopago.android.px.internal.util.TextUtil
import com.mercadopago.android.px.internal.viewmodel.DisableConfiguration
import com.mercadopago.android.px.internal.viewmodel.mappers.CardUiMapper
import com.mercadopago.android.px.internal.viewmodel.mappers.NonNullMapper
import com.mercadopago.android.px.model.CustomSearchItem
import com.mercadopago.android.px.model.ExpressMetadata
import java.util.*

class PaymentMethodDrawableItemMapper(
    private val chargeRepository: ChargeRepository,
    private val disabledPaymentMethodRepository: DisabledPaymentMethodRepository? = null,
    context: Context? = null) : NonNullMapper<ExpressMetadata?, DrawableFragmentItem?>() {

    private val disableConfiguration = context?.let { DisableConfiguration(it) }
    private var customSearchItems: List<CustomSearchItem> = Collections.emptyList()

    override fun map(expressMetadata: ExpressMetadata): DrawableFragmentItem? {
        val parameters = getParameters(expressMetadata)

        return when {
            expressMetadata.isCard -> SavedCardDrawableFragmentItem(parameters, expressMetadata.paymentMethodId,
                CardUiMapper(disableConfiguration).map(expressMetadata.card.displayInfo))
            expressMetadata.isAccountMoney -> AccountMoneyDrawableFragmentItem(parameters)
            expressMetadata.isConsumerCredits ->
                ConsumerCreditsDrawableFragmentItem(parameters, expressMetadata.consumerCredits)
            expressMetadata.isNewCard || expressMetadata.isOfflineMethods ->
                OtherPaymentMethodFragmentItem(parameters, expressMetadata.newCard, expressMetadata.offlineMethods)
            else -> null
        }
    }

    private fun getParameters(expressMetadata: ExpressMetadata): DrawableFragmentItem.Parameters {
        val charge = chargeRepository.getChargeRule(expressMetadata.paymentTypeId)
        val customOptionId = expressMetadata.customOptionId
        val (description, issuerName) = customSearchItems.firstOrNull() { c -> c.id == customOptionId }?.let {
            Pair(it.description.orEmpty(), it.issuer?.name.orEmpty())
        } ?: Pair(TextUtil.EMPTY, TextUtil.EMPTY)

        return DrawableFragmentItem.Parameters(
            customOptionId, expressMetadata.status, expressMetadata.displayInfo?.bottomDescription, charge?.message,
            expressMetadata.benefits?.reimbursement,
            disabledPaymentMethodRepository?.getDisabledPaymentMethod(customOptionId), description, issuerName)
    }

    fun setCustomSearchItems(items: List<CustomSearchItem>) {
        customSearchItems = items
    }
}