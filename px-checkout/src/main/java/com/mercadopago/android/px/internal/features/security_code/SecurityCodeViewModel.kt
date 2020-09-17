package com.mercadopago.android.px.internal.features.security_code

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.internal.base.BaseViewModel
import com.mercadopago.android.px.internal.features.pay_button.PayButton
import com.mercadopago.android.px.internal.features.security_code.model.VirtualCardInfo
import com.mercadopago.android.px.internal.repository.CardTokenRepository
import com.mercadopago.android.px.internal.repository.InitRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.internal.util.CVVRecoveryWrapper
import com.mercadopago.android.px.internal.util.TokenCreationWrapper
import com.mercadopago.android.px.internal.viewmodel.CardDrawerConfiguration
import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.model.CvvInfo
import com.mercadopago.android.px.model.PaymentRecovery
import com.mercadopago.android.px.model.internal.PaymentConfiguration
import com.mercadopago.android.px.tracking.internal.model.Reason
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SecurityCodeViewModel(
    private val cardTokenRepository: CardTokenRepository,
    private val escManagerBehaviour: ESCManagerBehaviour,
    private val paymentSettingRepository: PaymentSettingRepository,
    initRepository: InitRepository,
    userSelectionRepository: UserSelectionRepository,
    cardConfigurationMapper: CardConfigurationMapper,
    virtualCardInfoMapper: VirtualCardInfoMapper) : BaseViewModel() {

    private val cvvCardUiMutableLiveData = MutableLiveData<CardDrawerConfiguration>()
    val cvvCardUiLiveData:LiveData<CardDrawerConfiguration>
        get() = cvvCardUiMutableLiveData
    private val virtualCardInfoMutableLiveData = MutableLiveData<VirtualCardInfo>()
    val virtualCardInfoLiveData: LiveData<VirtualCardInfo>
        get() = virtualCardInfoMutableLiveData
    private val inputInfoMutableLiveData = MutableLiveData<Int>()
    val inputInfoLiveData: LiveData<Int>
        get() = inputInfoMutableLiveData
    private val tokenizeErrorApiMutableLiveData = MutableLiveData<Unit>()
    val tokenizeErrorApiLiveData: LiveData<Unit>
        get() = tokenizeErrorApiMutableLiveData

    private lateinit var paymentConfiguration: PaymentConfiguration
    private var paymentRecovery: PaymentRecovery? = null
    private var reason: Reason? = null
    private var cardUserSelection: Card = userSelectionRepository.card ?: error("Card selected not be null")
    private var cvvInfo: CvvInfo? = cardUserSelection.paymentMethod?.displayInfo?.cvvInfo

    init {
        cvvInfo?.let {
            virtualCardInfoMutableLiveData.value = virtualCardInfoMapper.map(it)
        } ?: CoroutineScope(Dispatchers.IO).launch {
            val initResponse = initRepository.loadInitResponse()
            val cardDisplayInfo = initResponse?.let { response ->
                val expressMetadata = response.express.find { data -> data.isCard && data.card.id == cardUserSelection.id }
                    ?: error("ExpressMetadata not be null")
                expressMetadata.card?.displayInfo
            } ?: error("DisplayInfo not be null")

            cvvCardUiMutableLiveData.postValue(cardConfigurationMapper.map(cardDisplayInfo))
        }

        inputInfoMutableLiveData.value = cardUserSelection.getSecurityCodeLength()
    }

    fun init(paymentConfiguration: PaymentConfiguration, paymentRecovery: PaymentRecovery?, reason: Reason?) {
        this.paymentConfiguration = paymentConfiguration
        this.paymentRecovery = paymentRecovery
        this.reason = reason
    }

    fun handlePrepayment(callback: PayButton.OnReadyForPaymentCallback) {
        callback.call(paymentConfiguration)
    }

    fun enqueueOnExploding(cvv: String, callback: PayButton.OnEnqueueResolvedCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = if (paymentRecovery != null) {
                CVVRecoveryWrapper(cardTokenRepository,
                    escManagerBehaviour,
                    paymentRecovery!!).recoverWithCVV(cvv)
            } else {
                TokenCreationWrapper
                    .Builder(cardTokenRepository, escManagerBehaviour)
                    .with(cardUserSelection)
                    .with(cardUserSelection.paymentMethod!!)
                    .build()
                    .createToken(cvv)
            }

            withContext(Dispatchers.Main) {
                response.resolve(success = { token ->
                    paymentSettingRepository.configure(token)
                    callback.success()
                }, error = {
                    tokenizeErrorApiMutableLiveData.value = Unit
                    callback.failure()
                })
            }
        }
    }
}