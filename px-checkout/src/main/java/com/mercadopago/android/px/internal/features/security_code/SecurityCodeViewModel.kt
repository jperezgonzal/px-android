package com.mercadopago.android.px.internal.features.security_code

import androidx.lifecycle.MutableLiveData
import com.meli.android.carddrawer.model.CardUI
import com.mercadopago.android.px.internal.base.BaseViewModel
import com.mercadopago.android.px.internal.repository.InitRepository
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.internal.viewmodel.CardDrawerConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SecurityCodeViewModel(initRepository: InitRepository,
                            userSelectionRepository: UserSelectionRepository,
                            cardConfigurationMapper: CardConfigurationMapper) : BaseViewModel() {

    val cvvCardUiLiveData = MutableLiveData<CardDrawerConfiguration>()

    init {
        val cardUserSelection = userSelectionRepository.card ?: error("")

        CoroutineScope(Dispatchers.IO).launch {

            val initResponse = initRepository.loadInitResponse()
            val cardDisplayInfo = initResponse?.let { response ->
                val cardMetaData = response.express.find { data -> data.isCard && data.card.id == cardUserSelection.id }?.card
                        ?: error("")
                cardMetaData.displayInfo
            } ?: error("")

            cvvCardUiLiveData.postValue(cardConfigurationMapper.map(cardDisplayInfo))
        }
    }
}