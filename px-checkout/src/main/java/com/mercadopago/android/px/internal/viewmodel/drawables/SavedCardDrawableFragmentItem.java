package com.mercadopago.android.px.internal.viewmodel.drawables;

import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.mercadopago.android.px.internal.viewmodel.CardUiConfiguration;

public class SavedCardDrawableFragmentItem extends DrawableFragmentItem {

    @NonNull public final String paymentMethodId;
    @NonNull public final CardUiConfiguration card;

    public static final Creator<SavedCardDrawableFragmentItem> CREATOR = new Creator<SavedCardDrawableFragmentItem>() {
        @Override
        public SavedCardDrawableFragmentItem createFromParcel(final Parcel in) {
            return new SavedCardDrawableFragmentItem(in);
        }

        @Override
        public SavedCardDrawableFragmentItem[] newArray(final int size) {
            return new SavedCardDrawableFragmentItem[size];
        }
    };

    public SavedCardDrawableFragmentItem(@NonNull final Parameters parameters, @NonNull final String paymentMethodId,
        @NonNull final CardUiConfiguration card) {
        super(parameters);
        this.paymentMethodId = paymentMethodId;
        this.card = card;
    }

    protected SavedCardDrawableFragmentItem(final Parcel in) {
        super(in);
        paymentMethodId = in.readString();
        card = in.readParcelable(CardUiConfiguration.class.getClassLoader());
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(paymentMethodId);
        dest.writeParcelable(card, flags);
    }

    @Override
    public Fragment draw(@NonNull final PaymentMethodFragmentDrawer drawer) {
        return drawer.draw(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}