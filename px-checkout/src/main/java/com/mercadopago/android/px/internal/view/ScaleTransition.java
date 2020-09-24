package com.mercadopago.android.px.internal.view;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;

public class ScaleTransition extends Transition {

    private static final String PROPNAME_SCALE_X = "PROPNAME_SCALE_X";
    private static final String PROPNAME_SCALE_Y = "PROPNAME_SCALE_Y";

    @Override
    public void captureStartValues(final TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(final TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    private void captureValues(final TransitionValues values) {
        values.values.put(PROPNAME_SCALE_X, values.view.getScaleX());
        values.values.put(PROPNAME_SCALE_Y, values.view.getScaleY());
    }

    @Nullable
    @Override
    public Animator createAnimator(final ViewGroup sceneRoot, final TransitionValues startValues, final TransitionValues endValues) {
        if(endValues == null || startValues == null) {
            return null;    // no values
        }

        final float startX = (float) startValues.values.get(PROPNAME_SCALE_X);
        final float startY = (float) startValues.values.get(PROPNAME_SCALE_Y);
        final float endX = (float) endValues.values.get(PROPNAME_SCALE_X);
        final float endY = (float) endValues.values.get(PROPNAME_SCALE_Y);

        if(startX == endX && startY == endY) {
            return null;    // no scale to run
        }

        final View view = startValues.view;
        final PropertyValuesHolder propX = PropertyValuesHolder.ofFloat(PROPNAME_SCALE_X, startX, endX);
        final PropertyValuesHolder propY = PropertyValuesHolder.ofFloat(PROPNAME_SCALE_Y, startY, endY);
        final ValueAnimator valAnim = ValueAnimator.ofPropertyValuesHolder(propX, propY);
        valAnim.addUpdateListener(valueAnimator -> {
            view.setPivotY(0);
            view.setScaleX((float) valueAnimator.getAnimatedValue(PROPNAME_SCALE_X));
            view.setScaleY((float) valueAnimator.getAnimatedValue(PROPNAME_SCALE_Y));
        });
        return valAnim;
    }
}