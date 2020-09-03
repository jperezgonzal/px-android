package com.mercadopago.android.px.internal.view;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.AttributeSet;
import com.mercadopago.android.px.R;

public final class ToolbarCancel extends Toolbar {

    public ToolbarCancel(final Context context) {
        this(context, null);
    }

    public ToolbarCancel(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToolbarCancel(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final int[] systemAttrs = {android.R.attr.layout_height};
        final TypedArray a = context.obtainStyledAttributes(attrs, systemAttrs);
        //Needs to be the same as height so icons are centered
        setMinimumHeight(a.getDimensionPixelSize(0, 0));
        a.recycle();
        init();
    }

    private void init() {
        final Activity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            final AppCompatActivity appCompatActivity = (AppCompatActivity)activity;
            appCompatActivity.setSupportActionBar(this);
            final ActionBar supportActionBar = appCompatActivity.getSupportActionBar();
            supportActionBar.setHomeAsUpIndicator(R.drawable.px_ic_close);
            supportActionBar.setHomeActionContentDescription(R.string.px_label_close);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
            setNavigationOnClickListener(v -> appCompatActivity.onBackPressed());
        }
    }

    @Nullable
    private Activity getActivity() {
        // Gross way of unwrapping the Activity so we can get the FragmentManager
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }
}