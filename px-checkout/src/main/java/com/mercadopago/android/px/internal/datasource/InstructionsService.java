package com.mercadopago.android.px.internal.datasource;

import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;
import com.mercadopago.android.px.internal.adapters.MPCallWrapper;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.internal.repository.InstructionsRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.services.InstructionsClient;
import com.mercadopago.android.px.model.Instruction;
import com.mercadopago.android.px.model.Instructions;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.services.Callback;
import java.util.ArrayList;
import java.util.List;

import static com.mercadopago.android.px.services.BuildConfig.API_ENVIRONMENT;

public class InstructionsService implements InstructionsRepository {

    /* default */ @NonNull final PaymentSettingRepository paymentSettingRepository;
    /* default */ @NonNull final InstructionsClient instructionsClient;
    /* default */ @NonNull final LongSparseArray<List<Instruction>> instructionsCache = new LongSparseArray<>();

    public InstructionsService(@NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final InstructionsClient instructionsClient) {
        this.paymentSettingRepository = paymentSettingRepository;
        this.instructionsClient = instructionsClient;
    }

    @Override
    public MPCall<List<Instruction>> getInstructions(@NonNull final PaymentResult paymentResult) {
        // Off payment method returns always 1 payment data.
        final String paymentTypeId = paymentResult.getPaymentData()
            .getPaymentMethod()
            .getPaymentTypeId();

        final Long paymentId = paymentResult.getPaymentId();
        if (instructionsCache.containsKey(paymentId)) {
            return new MPCall<List<Instruction>>() {
                @Override
                public void enqueue(final Callback<List<Instruction>> callback) {
                    callback.success(instructionsCache.get(paymentId));
                }

                @Override
                public void execute(final Callback<List<Instruction>> callback) {
                    callback.success(instructionsCache.get(paymentId));
                }
            };
        }
        return new MPCall<List<Instruction>>() {
            @Override
            public void enqueue(final Callback<List<Instruction>> callback) {
                getInstructionsCall(paymentTypeId, paymentId)
                    .enqueue(getCallback(callback, paymentId));
            }

            @Override
            public void execute(final Callback<List<Instruction>> callback) {
                getInstructionsCall(paymentTypeId, paymentId)
                    .execute(getCallback(callback, paymentId));
            }
        };
    }

    @NonNull
    private Callback<Instructions> getCallback(final Callback<List<Instruction>> callback, final Long id) {
        return new Callback<Instructions>() {
            @Override
            public void success(final Instructions instructions) {
                final List<Instruction> instructionList = resolveInstruction(instructions);
                instructionsCache.put(id, instructionList);
                callback.success(instructionList);
            }

            @Override
            public void failure(final ApiException apiException) {
                callback.failure(apiException);
            }
        };
    }

    /* default */
    @NonNull
    List<Instruction> resolveInstruction(final Instructions instructions) {
        return instructions == null ? new ArrayList<>()
            : (instructions.getInstructions() == null ? new ArrayList<>()
                : instructions.getInstructions());
    }

    /* default */ MPCall<Instructions> getInstructionsCall(final String paymentTypeId, final Long id) {
        return new MPCallWrapper<Instructions>() {
            @Override
            protected MPCall<Instructions> method() {
                return instructionsClient.getInstructions(API_ENVIRONMENT, id, paymentSettingRepository.getPublicKey(),
                    paymentSettingRepository.getPrivateKey(), paymentTypeId);
            }
        }.wrap();
    }
}