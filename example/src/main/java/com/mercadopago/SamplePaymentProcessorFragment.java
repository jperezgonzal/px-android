package com.mercadopago;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.core.SplitPaymentProcessor;
import com.mercadopago.example.R;

public class SamplePaymentProcessorFragment extends Fragment {

    private static final int CONST_TIME_MILLIS = 2000;
    private static final String ARG_PAYMENT = "arg_payment";

    public static SamplePaymentProcessorFragment with(@NonNull final Parcelable payment) {
        final SamplePaymentProcessorFragment fragment = new SamplePaymentProcessorFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARG_PAYMENT, payment);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    private SplitPaymentProcessor.OnPaymentListener paymentListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sample_payment_processor, container, false);
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof SplitPaymentProcessor.OnPaymentListener) {
            paymentListener = (SplitPaymentProcessor.OnPaymentListener) context;
        }
    }

    @Override
    public void onDetach() {
        paymentListener = null;
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().postDelayed(() -> {
            if (paymentListener != null && getArguments() != null && getArguments().containsKey(ARG_PAYMENT)) {
                paymentListener.onPaymentFinished(getArguments().getParcelable(ARG_PAYMENT));
            }
        }, CONST_TIME_MILLIS);
    }
}