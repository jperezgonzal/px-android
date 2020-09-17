package com.mercadopago.android.px.internal.features.security_code

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mercadopago.android.px.internal.base.BaseViewModel
import com.mercadopago.android.px.internal.features.pay_button.PayButton
import com.mercadopago.android.px.internal.features.security_code.model.VirtualCardInfo
import com.mercadopago.android.px.internal.features.security_code.use_case.DisplayInfoUseCase
import com.mercadopago.android.px.internal.base.use_case.TokenizeParams
import com.mercadopago.android.px.internal.base.use_case.TokenizeUseCase
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.viewmodel.CardDrawerConfiguration
import com.mercadopago.android.px.model.PaymentRecovery
import com.mercadopago.android.px.model.internal.PaymentConfiguration
import com.mercadopago.android.px.tracking.internal.model.Reason

class SecurityCodeViewModel(
    private val paymentSettingRepository: PaymentSettingRepository,
    private val tokenizeUseCase: TokenizeUseCase,
    displayInfoUseCase: DisplayInfoUseCase) : BaseViewModel() {

    private val cvvCardUiMutableLiveData = MutableLiveData<CardDrawerConfiguration>()
    val cvvCardUiLiveData: LiveData<CardDrawerConfiguration>
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

    init {
        displayInfoUseCase.execute(Unit, success = { triple ->
            val (cardDisplayInfo, cvvInfo, securityCodeLength) = triple
            cardDisplayInfo?.let { cvvCardUiMutableLiveData.postValue(CardDrawerConfiguration(cardDisplayInfo, null)) }
            cvvInfo?.let { virtualCardInfoMutableLiveData.value = VirtualCardInfo(it.title, it.message) }
            inputInfoMutableLiveData.value = securityCodeLength
        }, failure = {
            //TODO: Friction
        })
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
        tokenizeUseCase.execute(TokenizeParams(cvv, paymentRecovery),
            success = { token ->
                paymentSettingRepository.configure(token)
                callback.success()
            }, failure = {
            tokenizeErrorApiMutableLiveData.value = Unit
            callback.failure()
        })
    }
}