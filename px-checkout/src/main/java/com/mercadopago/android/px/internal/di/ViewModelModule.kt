package com.mercadopago.android.px.internal.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.fragment.app.Fragment

internal class ViewModelModule {
    private val factory = ViewModelFactory()
    fun <T : ViewModel?> get(fragment: Fragment, modelClass: Class<T>): T {
        return ViewModelProviders.of(fragment, factory).get(modelClass)
    }
}