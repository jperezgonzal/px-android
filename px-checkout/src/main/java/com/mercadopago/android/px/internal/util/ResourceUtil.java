package com.mercadopago.android.px.internal.util;

import android.content.Context;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.R;

public final class ResourceUtil {

    public static final int NEUTRAL_CARD_COLOR = R.color.px_white;
    public static final int FULL_TEXT_VIEW_COLOR = R.color.px_base_text_alpha;
    private static final String NEUTRAL_CARD_COLOR_NAME = "px_white";
    private static final String FULL_TEXT_VIEW_COLOR_NAME = "px_base_text_alpha";
    private static final String DYNAMIC_SUFFIX = "_dynamic";

    private ResourceUtil() {
    }

    public static int getCardColor(final String paymentMethodId, final Context context) {
        final String colorName = "px_" + paymentMethodId.toLowerCase();
        int color = context.getResources().getIdentifier(colorName, "color", context.getPackageName());
        if (color == 0) {
            color = context.getResources().getIdentifier(NEUTRAL_CARD_COLOR_NAME, "color", context.getPackageName());
        }
        return color;
    }

    public static int getCardFontColor(final String paymentMethodId, final Context context) {
        if (TextUtil.isEmpty(paymentMethodId)) {
            return FULL_TEXT_VIEW_COLOR;
        }
        final String colorName = "px_font_" + paymentMethodId.toLowerCase();
        int color = context.getResources().getIdentifier(colorName, "color", context.getPackageName());
        if (color == 0) {
            color = context.getResources().getIdentifier(FULL_TEXT_VIEW_COLOR_NAME, "color", context.getPackageName());
        }
        return color;
    }

    public static int getCardImage(@NonNull final Context context, @NonNull final String paymentMethodId) {
        final String imageName = "px_ico_card_" + paymentMethodId.toLowerCase() + DYNAMIC_SUFFIX;
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }
}