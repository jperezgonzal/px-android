package com.mercadopago.android.px.internal.features.security_code

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.meli.android.carddrawer.model.CardDrawerView
import com.mercadolibre.android.andesui.snackbar.action.AndesSnackbarAction
import com.mercadolibre.android.andesui.textfield.AndesTextfieldCode
import com.mercadopago.android.px.R
import com.mercadopago.android.px.core.BackHandler
import com.mercadopago.android.px.internal.di.viewModel
import com.mercadopago.android.px.internal.extensions.postDelayed
import com.mercadopago.android.px.internal.extensions.runWhenLaidOut
import com.mercadopago.android.px.internal.extensions.showSnackBar
import com.mercadopago.android.px.internal.features.express.RenderMode
import com.mercadopago.android.px.internal.features.pay_button.PayButton
import com.mercadopago.android.px.internal.features.pay_button.PayButtonFragment
import com.mercadopago.android.px.internal.features.security_code.model.SecurityCodeParams
import com.mercadopago.android.px.internal.util.ViewUtils
import com.mercadopago.android.px.internal.util.nonNullObserve
import com.mercadopago.android.px.internal.view.animator.AnimatorFactory
import com.mercadopago.android.px.model.exceptions.MercadoPagoError

internal class SecurityCodeFragment : Fragment(), PayButton.Handler, BackHandler {

    private val securityCodeViewModel: SecurityCodeViewModel by viewModel()

    private lateinit var cvvEditText: AndesTextfieldCode
    private lateinit var cvvTitle: TextView
    private lateinit var payButtonFragment: PayButtonFragment
    private lateinit var cvvToolbar: Toolbar
    private lateinit var renderMode: RenderMode
    private lateinit var cardDrawer: CardDrawerView
    private lateinit var cvvSubtitle: TextView
    private var fragmentContainer: Int = 0

    private var cardAnimationDistance: Float? = null
    private var shouldAnimate = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        arguments?.getParcelable<SecurityCodeParams>(ARG_PARAMS)?.let {
            fragmentContainer = it.fragmentContainer
            defineRenderMode(it.renderMode)

            val view = inflater.inflate(
                if (renderMode == RenderMode.LOW_RES) {
                    R.layout.fragment_security_code_lowres
                } else {
                    R.layout.fragment_security_code
                },
                container, false
            )

            cvvToolbar = view.findViewById(R.id.cvv_toolbar)
            cardDrawer = view.findViewById(R.id.card_drawer)
            cvvEditText = view.findViewById(R.id.cvv_edit_text)
            cvvTitle = view.findViewById(R.id.cvv_title)
            cvvSubtitle = view.findViewById(R.id.cvv_subtitle)

            if (renderMode == RenderMode.NO_CARD) {
                cardDrawer.visibility = GONE
                cvvSubtitle.visibility = VISIBLE
            }

            return view
        } ?: error("Arguments not be null")
    }

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        if (shouldAnimate) {
            if (enter) {
                animateEnter()
            } else {
                animateExit()
            }
        } else {
            cvvEditText.runWhenLaidOut {
                cardDrawer.pivotX = cardDrawer.measuredWidth * 0.5f
                cardDrawer.pivotY = 0f
                cardDrawer.scaleX = 0.5f
                cardDrawer.scaleY = 0.5f
                postAnimationConfig()
            }
        }
        return super.onCreateAnimator(transit, enter, nextAnim)
    }

    private fun animateEnter() {
        cvvEditText.runWhenLaidOut {
            cardDrawer.pivotX = cardDrawer.measuredWidth * 0.5f
            cardDrawer.pivotY = 0f
            cardAnimationDistance = getTopForAnimation(cardDrawer)
            val cardAnim = AnimatorFactory.scaleAndTranslateY(cardDrawer, 0.5f, cardAnimationDistance!! * -1f, duration = 600)
            cvvToolbar.alpha = 0f
            val toolbarAnim = AnimatorFactory.fadeIn(cvvToolbar, 600)
            cvvTitle.alpha = 0f
            val titleAnim = AnimatorFactory.fadeInAndTranslateY(cvvTitle, cvvTitle.measuredHeight * -1f, 0f, 600)
            cvvSubtitle.alpha = 0f
            val subtitleAnim = AnimatorFactory.fadeInAndTranslateY(cvvSubtitle, cvvSubtitle.measuredHeight * -1f, 0f, 600)
            cvvEditText.alpha = 0f
            val textFieldAnim = AnimatorFactory.fadeInAndTranslateY(cvvEditText, cvvEditText.measuredHeight.toFloat(), 0f, 300)
            val buttonView = view!!.findViewById<View>(R.id.pay_button)
            buttonView.alpha = 0f
            val buttonAnim = AnimatorFactory.fadeInAndTranslateY(buttonView, buttonView.measuredHeight.toFloat(), 0f, 600)

            titleAnim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    cardDrawer.translationY = 0f
                    postAnimationConfig()
                }
            })

            AnimatorSet().apply {
                play(cardAnim)
                play(toolbarAnim).with(titleAnim).with(subtitleAnim).after(800)
                play(buttonAnim).after(toolbarAnim)
                play(textFieldAnim).after(buttonAnim)
                start()
            }
        }
    }

    private fun postAnimationConfig() {
        ConstraintSet().apply {
            val constraint = view as ConstraintLayout
            clone(constraint)
            connect(cardDrawer.id, ConstraintSet.TOP, cvvSubtitle.id, ConstraintSet.BOTTOM)
            applyTo(constraint)
            cardDrawer.showSecurityCode()
            ViewUtils.openKeyboard(cvvEditText)
        }
    }

    private fun getTopForAnimation(view: View): Float {
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        return view.top.toFloat() - params.topMargin - cvvTitle.bottom
    }

    private fun animateExit() {
        cvvEditText.runWhenLaidOut {
            cardDrawer.showFront()
            val cardAnim = AnimatorFactory.scaleAndTranslateY(cardDrawer, 1f, cardAnimationDistance ?: 0f)
            val toolAnim = AnimatorFactory.fadeOut(cvvToolbar)
            val titleAnim = AnimatorFactory.fadeOutAndTranslateY(cvvTitle, cvvTitle.measuredHeight * -1f)
            val subtitleAnim = AnimatorFactory.fadeOutAndTranslateY(cvvSubtitle, cvvSubtitle.measuredHeight * -1f)
            val textFieldAnim =
                AnimatorFactory.fadeOutAndTranslateY(cvvEditText, cvvEditText.measuredHeight.toFloat(), duration = 300)
            val buttonView = view!!.findViewById<View>(R.id.pay_button)
            val buttonAnim = AnimatorFactory.translateY(buttonView,
                view!!.height - buttonView.top - resources.getDimension(R.dimen.px_m_margin) - buttonView.height)

            AnimatorSet().apply {
                play(cardAnim).with(toolAnim).with(titleAnim).with(subtitleAnim).with(textFieldAnim).with(buttonAnim)
                duration = 600
                start()
            }
        }
    }

    private fun defineRenderMode(parentRenderMode: RenderMode) {
        val availableHeight = resources.configuration.screenHeightDp
        renderMode = when(parentRenderMode) {
            RenderMode.HIGH_RES -> if (availableHeight >= HIGH_RES_MIN_HEIGHT) RenderMode.HIGH_RES else RenderMode.NO_CARD
            RenderMode.LOW_RES -> if (availableHeight >= LOW_RES_MIN_HEIGHT) RenderMode.LOW_RES else RenderMode.NO_CARD
            else -> RenderMode.NO_CARD
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.apply {
            cardAnimationDistance = getFloat(EXTRA_ANIM_DISTANCE)
            shouldAnimate = false
        }

        (activity as? AppCompatActivity?)?.apply {
            setSupportActionBar(cvvToolbar)
            supportActionBar?.apply {
                setDisplayShowTitleEnabled(false)
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                setHomeButtonEnabled(true)
                cvvToolbar.setNavigationOnClickListener {
                    val buttonView = view.findViewById<View>(R.id.pay_button)
                    //Prevent button going down with keyboard
                    ObjectAnimator.ofFloat(buttonView, "y", buttonView.top.toFloat()).start()
                    ViewUtils.hideKeyboard(activity)
                    postDelayed(100) {
                        onBackPressed()
                    }
                }
            }
        }

        arguments?.getParcelable<SecurityCodeParams>(ARG_PARAMS)?.let {
            securityCodeViewModel.init(it.paymentConfiguration, it.paymentRecovery, it.reason)
        } ?: error("Arguments not be null")

        payButtonFragment = childFragmentManager.findFragmentById(R.id.pay_button) as PayButtonFragment
        observeViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        cardAnimationDistance?.let { outState.putFloat(EXTRA_ANIM_DISTANCE, it) }
    }

    private fun observeViewModel() {
        with(securityCodeViewModel) {
            cvvCardUiLiveData.nonNullObserve(viewLifecycleOwner) {
                with(cardDrawer) {
                    card.name = it.name
                    card.expiration = it.date
                    card.number = it.number
                    show(it)
                }
            }

            virtualCardInfoLiveData.nonNullObserve(viewLifecycleOwner) {
                cardDrawer.visibility = GONE
                cvvTitle.text = it.title
                with(cvvSubtitle) {
                    text = it.message
                    visibility = VISIBLE
                }
            }

            tokenizeErrorApiLiveData.nonNullObserve(viewLifecycleOwner) {
                val action = AndesSnackbarAction(
                    getString(R.string.px_snackbar_error_action), View.OnClickListener {
                    activity?.onBackPressed()
                })
                view.showSnackBar(getString(R.string.px_error_title), andesSnackbarAction = action)
            }

            inputInfoLiveData.nonNullObserve(viewLifecycleOwner) {
                //cvvEditText.filters = arrayOf<InputFilter>(LengthFilter(it))
            }
        }
    }

    override fun prePayment(callback: PayButton.OnReadyForPaymentCallback) {
        securityCodeViewModel.handlePrepayment(callback)
    }

    override fun enqueueOnExploding(callback: PayButton.OnEnqueueResolvedCallback) {
        securityCodeViewModel.enqueueOnExploding(cvvEditText.text.toString(), callback)
    }

    override fun onPaymentError(error: MercadoPagoError) {
        securityCodeViewModel.onPaymentError()
    }

    override fun onCvvRequested() = PayButton.CvvRequestedModel(fragmentContainer, renderMode)

    override fun handleBack() = payButtonFragment.isExploding().also { isExploding ->
        if (!isExploding) {
            securityCodeViewModel.onBack()
        }
    }

    companion object {
        const val TAG = "security_code"
        private const val EXTRA_ANIM_DISTANCE = "bundle_anim_distance"
        private const val ARG_PARAMS = "security_code_params"
        private const val HIGH_RES_MIN_HEIGHT = 620
        private const val LOW_RES_MIN_HEIGHT = 585

        @JvmStatic
        fun newInstance(params: SecurityCodeParams): SecurityCodeFragment {
            return SecurityCodeFragment().also {
                it.arguments = Bundle().apply {
                    putParcelable(ARG_PARAMS, params)
                }
            }
        }
    }
}