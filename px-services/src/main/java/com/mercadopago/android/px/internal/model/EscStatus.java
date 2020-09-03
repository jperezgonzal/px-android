package com.mercadopago.android.px.internal.model;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
    EscStatus.APPROVED,
    EscStatus.REJECTED,
    EscStatus.NOT_AVAILABLE
})
public @interface EscStatus {
    String APPROVED = "approved";
    String REJECTED = "rejected";
    String NOT_AVAILABLE = "not_available";
}