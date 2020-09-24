package com.mercadopago;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.mercadopago.android.px.BuildConfig;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.addons.FakeEscManagerBehaviourImpl;
import com.mercadopago.android.px.addons.FakeLocaleBehaviourImpl;
import com.mercadopago.android.px.addons.MockSecurityBehaviour;
import com.mercadopago.android.px.addons.PXBehaviourConfigurer;
import com.mercadopago.android.px.di.Dependencies;
import com.mercadopago.android.px.font.FontConfigurator;
import com.mercadopago.android.px.internal.util.HttpClientUtil;
import okhttp3.OkHttpClient;

public class SampleApplication extends Application {

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
    }

    private void initialize() {
        Fresco.initialize(this);
        Stetho.initializeWithDefaults(this);

        // Create client base, add interceptors
        final OkHttpClient.Builder baseClient = HttpClientUtil.createBaseClient(this, 10, 10, 10)
            .addNetworkInterceptor(new StethoInterceptor());

        // customClient: client with TLS protocol setted
        final OkHttpClient customClient = HttpClientUtil.enableTLS12(baseClient)
            .build();

        HttpClientUtil.setCustomClient(customClient);

        Dependencies.getInstance().initialize(getApplicationContext());

        final ESCManagerBehaviour escManagerBehaviour = new FakeEscManagerBehaviourImpl();
        final PXBehaviourConfigurer builder = new PXBehaviourConfigurer();
        if (BuildConfig.DEBUG) {
            builder.with(new MockSecurityBehaviour(escManagerBehaviour));
        }
        builder.with(escManagerBehaviour)
            .with(FakeLocaleBehaviourImpl.INSTANCE)
            .configure();

        FontConfigurator.configure();
    }
}