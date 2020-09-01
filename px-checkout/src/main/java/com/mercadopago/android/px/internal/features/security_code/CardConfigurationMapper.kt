package com.mercadopago.android.px.internal.features.security_code

import com.meli.android.carddrawer.model.CardUI
import com.mercadopago.android.px.internal.viewmodel.CardDrawerConfiguration
import com.mercadopago.android.px.internal.viewmodel.mappers.Mapper
import com.mercadopago.android.px.model.CardDisplayInfo

class CardConfigurationMapper: Mapper<CardDisplayInfo, CardUI>() {
    override fun map(model: CardDisplayInfo) = model.run { CardDrawerConfiguration(model,  null) }
}