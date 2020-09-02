package com.mercadopago.android.px.internal.features.security_code

import androidx.lifecycle.MutableLiveData
import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.internal.base.BaseViewModel
import com.mercadopago.android.px.internal.features.pay_button.PayButton
import com.mercadopago.android.px.internal.repository.CardTokenRepository
import com.mercadopago.android.px.internal.repository.InitRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.internal.util.TokenCreationWrapper
import com.mercadopago.android.px.internal.viewmodel.CardDrawerConfiguration
import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.model.internal.FromExpressMetadataToPaymentConfiguration as PaymentConfigurationMapper
import com.mercadopago.android.px.model.internal.PaymentConfiguration
import com.mercadopago.android.px.tracking.internal.model.Reason
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SecurityCodeViewModel(initRepository: InitRepository,
                            userSelectionRepository: UserSelectionRepository,
                            private val cardTokenRepository: CardTokenRepository,
                            private val escManagerBehaviour: ESCManagerBehaviour,
                            private val paymentSettingRepository: PaymentSettingRepository,
                            cardConfigurationMapper: CardConfigurationMapper) : BaseViewModel() {

    val cvvCardUiLiveData = MutableLiveData<CardDrawerConfiguration>()

    private lateinit var paymentConfiguration: PaymentConfiguration
    private lateinit var reason: Reason
    private var cardUserSelection: Card = userSelectionRepository.card ?: error("")

    init {

        CoroutineScope(Dispatchers.IO).launch {
            val initResponse = initRepository.loadInitResponse()
            val cardDisplayInfo = initResponse?.let { response ->
                val expressMetadata = response.express.find { data -> data.isCard && data.card.id == cardUserSelection.id }
                        ?: error("")
                expressMetadata.card?.displayInfo
            } ?: error("")

            cvvCardUiLiveData.postValue(cardConfigurationMapper.map(cardDisplayInfo))
        }
    }

    fun init(paymentConfiguration: PaymentConfiguration, reason: Reason) {
        this.paymentConfiguration = paymentConfiguration
        this.reason = reason
    }

    fun handlePrePaymentFinished(callback: PayButton.OnReadyForPaymentCallback) {
        callback.call(paymentConfiguration)
    }

    fun enqueueOnExploding(cvv: String, callback: PayButton.OnEnqueueResolvedCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val token = TokenCreationWrapper
                    .Builder(cardTokenRepository, escManagerBehaviour)
                    .with(cardUserSelection)
                    .with(cardUserSelection.paymentMethod!!)
                    .build()
                    .createToken(cvv)
            paymentSettingRepository.configure(token)
            withContext(Dispatchers.Main) {
                callback.success()
            }
        }
    }
}