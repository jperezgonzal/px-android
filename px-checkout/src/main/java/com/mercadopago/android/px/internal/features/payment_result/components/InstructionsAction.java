package com.mercadopago.android.px.internal.features.payment_result.components;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.mercadolibre.android.ui.widgets.MeliButton;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.internal.actions.CopyAction;
import com.mercadopago.android.px.internal.actions.LinkAction;
import com.mercadopago.android.px.model.Action;
import com.mercadopago.android.px.model.InstructionAction;

import static com.mercadopago.android.px.model.InstructionAction.Tags.COPY;
import static com.mercadopago.android.px.model.InstructionAction.Tags.LINK;

public class InstructionsAction extends CompactComponent<InstructionsAction.Prop, ActionDispatcher> {

    /* default */ InstructionsAction(@NonNull final Prop props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public static class Prop {
        public final InstructionAction instructionAction;

        /* default */ Prop(@NonNull final Builder builder) {
            instructionAction = builder.instructionAction;
        }

        public Builder toBuilder() {
            return new Prop.Builder()
                .setInstructionAction(instructionAction);
        }

        public static final class Builder {
            InstructionAction instructionAction;

            /* default */ Builder setInstructionAction(final InstructionAction instructionAction) {
                this.instructionAction = instructionAction;
                return this;
            }

            public Prop build() {
                return new Prop(this);
            }
        }
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        final Context context = parent.getContext();
        final MeliButton button = new MeliButton(context);
        button.setType(MeliButton.Type.OPTION_PRIMARY);

        final Action action = getAction();
        if (action != null) {
            button.setText(props.instructionAction.getLabel());
            button.setOnClickListener(v -> getActions().dispatch(action));
            parent.addView(button);
        }

        return parent;
    }

    @Nullable
    private Action getAction() {
        final InstructionAction action = props.instructionAction;
        switch (action.getTag()) {
        case COPY:
            return new CopyAction(action.getContent());
        case LINK:
            return new LinkAction(action.getUrl());
        default:
            return null;
        }
    }
}