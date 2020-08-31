package com.mercadopago.android.px.internal.features.security_code

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mercadopago.android.px.R

class SecurityCodeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_security_code, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SecurityCodeFragment()
        const val TAG = "security_code"
    }
}