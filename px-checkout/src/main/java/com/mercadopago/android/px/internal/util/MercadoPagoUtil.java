package com.mercadopago.android.px.internal.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.model.Bin;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.exceptions.BinException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public final class MercadoPagoUtil {

    private static final String SDK_PREFIX = "px_";
    private static final String PACKAGE_NAME_MP = "com.mercadopago.wallet";
    private static final String PLATFORM_MP = "MP";
    private static final String PLATFORM_ML = "ML";
    private static final String INTERNAL = "INTERNAL";
    private static final String MP_WEBVIEW_DEEPLINK = "mercadopago://webview/?url=";
    private static final String ML_WEBVIEW_DEEPLINK = "meli://webview/?url=";

    private MercadoPagoUtil() {
    }

    public static int getPaymentMethodIcon(final Context context, final String paymentMethodId) {
        return getPaymentMethodPicture(context, SDK_PREFIX, paymentMethodId);
    }

    private static int getPaymentMethodPicture(final Context context, final String type, String paymentMethodId) {
        int resource;
        paymentMethodId = type + paymentMethodId;
        try {
            resource = context.getResources().getIdentifier(paymentMethodId, "drawable", context.getPackageName());
        } catch (final Exception e) {
            try {
                resource =
                    context.getResources().getIdentifier(SDK_PREFIX + "bank", "drawable", context.getPackageName());
            } catch (final Exception ex) {
                resource = 0;
            }
        }
        return resource;
    }

    public static boolean isCard(final String paymentTypeId) {
        return (paymentTypeId != null) &&
            (paymentTypeId.equals(PaymentTypes.CREDIT_CARD) || paymentTypeId.equals(PaymentTypes.DEBIT_CARD) ||
                paymentTypeId.equals(PaymentTypes.PREPAID_CARD));
    }

    public static String getAccreditationTimeMessage(final Context context, final int seconds) {
        if (seconds == 0) {
            return context.getString(R.string.px_instant_accreditation_time);
        } else if (seconds <= 1380) {
            final int hours = new BigDecimal(seconds / 60f).setScale(0, RoundingMode.UP).intValue();
            return TextUtil.format(context, R.plurals.px_accreditation_time_hour, hours,
                String.valueOf(hours));
        } else {
            final int days = new BigDecimal(seconds / (60f * 24f)).setScale(0, RoundingMode.UP).intValue();
            return TextUtil.format(context, R.plurals.px_accreditation_time_working_day, days, String.valueOf(days));
        }
    }

    public static List<PaymentMethod> getValidPaymentMethodsForBin(final String bin,
        final Iterable<PaymentMethod> paymentMethods) {
        if (bin.length() == Bin.BIN_LENGTH) {
            final List<PaymentMethod> validPaymentMethods = new ArrayList<>();
            for (final PaymentMethod pm : paymentMethods) {
                if (pm.isValidForBin(bin)) {
                    validPaymentMethods.add(pm);
                }
            }
            return validPaymentMethods;
        }
        throw new BinException(bin.length());
    }

    public static boolean isMPInstalled(final PackageManager packageManager) {
        try {
            return packageManager != null && packageManager.getApplicationInfo(PACKAGE_NAME_MP, 0).enabled;
        } catch (final PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isMP(@NonNull final Context context) {
        return getPlatform(context).equals(PLATFORM_MP);
    }

    public static String getPlatform(@NonNull final Context context) {
        final String packageName = context.getApplicationInfo().packageName;
        return packageName.contains("com.mercadolibre") ? PLATFORM_ML : PLATFORM_MP;
    }

    public static Intent getSafeIntent(@NonNull final Context context, @NonNull final Uri uri) {
        return getSafeIntent(context).setData(uri);
    }

    private static Intent getSafeIntent(@NonNull final Context context) {
        return new Intent(Intent.ACTION_VIEW).setPackage(context.getPackageName()).putExtra(INTERNAL, true);
    }

    @Nullable
    public static Intent getIntent(@NonNull final Context context, @NonNull final String link) {
        final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(link));
        if (context.getPackageManager().queryIntentActivities(intent, 0).isEmpty()) {
            return null;
        } else {
            return intent;
        }
    }

    @Nullable
    public static Intent getNativeOrWebViewIntent(@NonNull final Context context, @NonNull final String link) {
        if (link.startsWith("http")) {
            final String webViewPath = isMP(context) ? MP_WEBVIEW_DEEPLINK : ML_WEBVIEW_DEEPLINK;
            final Intent intent = getIntent(context, webViewPath + link);
            if (intent != null) {
                return intent;
            }
        }
        return getIntent(context, link);
    }
}