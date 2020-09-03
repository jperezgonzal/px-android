package com.mercadopago.android.px.internal.features.security_code

import com.mercadopago.android.px.internal.features.security_code.model.VirtualCardInfo
import com.mercadopago.android.px.internal.viewmodel.mappers.Mapper
import com.mercadopago.android.px.model.CvvInfo

class VirtualCardInfoMapper: Mapper<CvvInfo, VirtualCardInfo>() {
    override fun map(model: CvvInfo) = model.run { VirtualCardInfo(title, message) }
}