package com.mercadopago.android.px.internal.features.express.installments;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.font.FontHelper;
import com.mercadopago.android.px.internal.font.PxFont;
import com.mercadopago.android.px.internal.util.CurrenciesUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.internal.Text;
import java.util.Locale;

public class InstallmentRowHolder extends RecyclerView.ViewHolder {

    private final View container;
    private final TextView installmentsText;
    private final MPTextView reimbursement;
    private final MPTextView installmentsInterestTop;
    private final MPTextView installmentsInterestCenter;

    /* default */ InstallmentRowHolder(final View itemView) {
        super(itemView);
        container = itemView.findViewById(R.id.container);
        installmentsText = itemView.findViewById(R.id.mpsdkInstallmentsText);
        installmentsInterestTop = itemView.findViewById(R.id.mpsdkInstallmentsInterestTop);
        reimbursement = itemView.findViewById(R.id.mpsdkReimbursement);
        installmentsInterestCenter = itemView.findViewById(R.id.mpsdkInstallmentsInterest);
    }

    /* default */ void populate(final InstallmentsAdapter.ItemListener itemListener, @NonNull final Model model) {
        final int hiddenVisibility = model.showBigRow ? View.INVISIBLE : View.GONE;
        setInstallmentsText(model.currency, model.payerCost);
        final boolean hasReimbursement = loadReimbursement(model.reimbursement, hiddenVisibility);
        loadInstallmentsInterest(model, hasReimbursement, hiddenVisibility);
        hideUnusedViews(hasReimbursement, hiddenVisibility);
        itemView.setOnClickListener(v -> itemListener.onClick(model.payerCost));
    }

    private boolean loadReimbursement(@Nullable final Text reimbursementText, final int visibility) {
        return ViewUtils.loadOrHide(visibility, reimbursementText, reimbursement);
    }

    private void loadInstallmentsInterest(@NonNull final Model model,
        final boolean hasReimbursement, final int visibility) {
        final MPTextView installmentsInterest = hasReimbursement ? installmentsInterestTop : installmentsInterestCenter;
        final boolean interestFree = ViewUtils.loadOrHide(visibility, model.interestFree, installmentsInterest);
        if (!interestFree) {
            ViewUtils.loadOrGone(getAmountWithRateText(model.currency, model.payerCost), installmentsInterest);
            installmentsInterest
                .setTextColor(ContextCompat.getColor(installmentsInterest.getContext(), R.color.px_color_payer_costs));
            FontHelper.setFont(installmentsInterest, PxFont.REGULAR);
            installmentsInterest.setContentDescription(
                TextUtils.concat(String.valueOf(model.payerCost.getTotalAmount().floatValue()),
                    installmentsInterest.getContext().getString(R.string.px_money)));
        }
    }

    private void hideUnusedViews(final boolean hasReimbursement, final int visibility) {
        final MPTextView viewToHide = hasReimbursement ? installmentsInterestCenter : installmentsInterestTop;
        viewToHide.setVisibility(visibility);
    }

    private CharSequence getAmountWithRateText(@NonNull final Currency currency, @NonNull final PayerCost payerCost) {
        final Spanned spannedInstallmentsText =
            CurrenciesUtil.getSpannedAmountWithCurrencySymbol(payerCost.getTotalAmount(), currency);
        return TextUtils.concat("(", spannedInstallmentsText, ")");
    }

    private void setInstallmentsText(@NonNull final Currency currency, @NonNull final PayerCost payerCost) {

        final Spanned spannedInstallmentsText =
            CurrenciesUtil.getSpannedAmountWithCurrencySymbol(payerCost.getInstallmentAmount(), currency);

        final String text = installmentsText.getContext().getString(R.string.px_installments_by);

        final String installmentText = String.format(Locale.getDefault(), "%d", payerCost.getInstallments());
        final Context context = itemView.getContext();

        installmentsText.setText(new SpannableStringBuilder(installmentText)
            .append(text)
            .append(TextUtil.SPACE)
            .append(spannedInstallmentsText));

        installmentsText.setContentDescription(new SpannableStringBuilder(installmentText)
            .append(context.getString(R.string.px_date_divider))
            .append(TextUtil.SPACE)
            .append(String.valueOf(payerCost.getInstallmentAmount().floatValue()))
            .append(context.getString(R.string.px_money)));
    }

    /* default */ void highLight() {
        container.setSelected(true);
    }

    /* default */ void noHighLight() {
        container.setSelected(false);
    }

    public static final class Model {
        @NonNull /* default */ final PayerCost payerCost;
        @NonNull /* default */ final Currency currency;
        @Nullable /* default */ final Text interestFree;
        @Nullable /* default */ final Text reimbursement;
        /* default */ final boolean showBigRow;

        public Model(@NonNull final PayerCost payerCost, @NonNull final Currency currency,
            @Nullable final Text interestFree, @Nullable final Text reimbursement, final boolean showBigRow) {
            this.payerCost = payerCost;
            this.currency = currency;
            this.interestFree = interestFree;
            this.reimbursement = reimbursement;
            this.showBigRow = showBigRow;
        }
    }
}