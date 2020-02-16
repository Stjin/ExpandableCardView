package host.stjin.expandablecardview


import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.Transformation
import android.widget.CompoundButton
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.expandable_cardview.view.*


/**
 *
 * Copyright (c) 2018 Alessandro Sperotti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Created by alessandros on 23/02/2018.
 * Modified by Adrian Devezin on 24/12/2018.
 * Modified by Stjin on 01/02/2020.
 * @author Alessandro Sperotti
 */

class ExpandableCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    private var title: String? = null

    private var innerView: View? = null

    private var typedArray: TypedArray? = null
    private var innerViewRes: Int = 0
    private var cardStrokeColor: Int = android.R.color.transparent
    private var cardArrowColor: Int = android.R.color.darker_gray
    private var cardTextColor: Int = android.R.color.darker_gray
    private var cardStrokeWidth: Int = 0
    private var cardTitleSize: Float = 0f
    private var cardRadius: Float = 4f
    private var cardElevation: Float = 4f
    private var cardRipple: Boolean = false
    private var expandableCardTitleBold: Boolean = false
    private var iconDrawable: Drawable? = null

    private var animDuration = DEFAULT_ANIM_DURATION.toLong()

    private var isExpanded = false
        private set
    private var isExpanding = false
    private var isCollapsing = false
    private var expandOnClick = false
    private var showSwitch = false
    private var startExpanded = false

    private var previousHeight = 0

    private var listener: OnExpandedListener? = null
    private var switchListener: OnSwitchChangeListener? = null

    private val defaultClickListener = OnClickListener {
        if (isExpanded)
            collapse()
        else
            expand()
    }

    private val switchOnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { p0, p1 -> switchListener?.onSwitchChanged(p0, p1) }

    private val isMoving: Boolean
        get() = isExpanding || isCollapsing


    init {
        initView(context)
        attrs?.let {
            initAttributes(context, attrs)
        }
    }

    private fun initView(context: Context) {
        //Inflating View
        LayoutInflater.from(context).inflate(R.layout.expandable_cardview, this)
    }

    private fun initAttributes(context: Context, attrs: AttributeSet) {
        //Ottengo attributi
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableCardView)
        this@ExpandableCardView.typedArray = typedArray
        title = typedArray.getString(R.styleable.ExpandableCardView_title)
        iconDrawable = typedArray.getDrawable(R.styleable.ExpandableCardView_icon)
        innerViewRes = typedArray.getResourceId(R.styleable.ExpandableCardView_inner_view, View.NO_ID)
        expandOnClick = typedArray.getBoolean(R.styleable.ExpandableCardView_expandOnClick, false)
        showSwitch = typedArray.getBoolean(R.styleable.ExpandableCardView_showSwitch, false)
        animDuration = typedArray.getInteger(R.styleable.ExpandableCardView_animationDuration, DEFAULT_ANIM_DURATION).toLong()
        startExpanded = typedArray.getBoolean(R.styleable.ExpandableCardView_startExpanded, false)
        cardRipple = typedArray.getBoolean(R.styleable.ExpandableCardView_expandableCardRipple, false)
        expandableCardTitleBold = typedArray.getBoolean(R.styleable.ExpandableCardView_expandableCardTitleBold, false)
        cardStrokeColor = typedArray.getInteger(R.styleable.ExpandableCardView_expandableCardStrokeColor, android.R.color.transparent)
        cardArrowColor = typedArray.getInteger(R.styleable.ExpandableCardView_expandableCardArrowColor, android.R.color.black)
        cardStrokeWidth = typedArray.getInteger(R.styleable.ExpandableCardView_expandableCardStrokeWidth, 0)
        cardTitleSize = typedArray.getFloat(R.styleable.ExpandableCardView_expandableCardTitleSize, 0f)
        cardElevation = typedArray.getFloat(R.styleable.ExpandableCardView_expandableCardElevation, 4f)
        cardRadius = typedArray.getFloat(R.styleable.ExpandableCardView_expandableCardRadius, 4f)
        cardTextColor = typedArray.getInteger(R.styleable.ExpandableCardView_expandableCardTitleColor, android.R.color.darker_gray)

        typedArray.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        //Setting attributes
        if (!TextUtils.isEmpty(title)) card_title.text = title
        if (cardTextColor != android.R.color.darker_gray) card_title.setTextColor(cardTextColor)
        if (cardStrokeColor != android.R.color.transparent) card_layout.strokeColor = cardStrokeColor
        if (cardStrokeWidth != 0) card_layout.strokeWidth = cardStrokeWidth
        if (cardTitleSize != 0f) card_title.textSize = cardTitleSize
        if (!cardRipple) card_layout.rippleColor = ContextCompat.getColorStateList(context, android.R.color.transparent)

        if (expandableCardTitleBold) {
            card_title.setTypeface(null, Typeface.BOLD)
        }

        card_switch.visibility = if (showSwitch) View.VISIBLE else View.GONE
        card_layout.radius = Utils.convertPixelsToDp(cardRadius, context)
        card_layout.cardElevation = Utils.convertPixelsToDp(cardElevation, context)


        if (cardArrowColor != android.R.color.black) card_arrow.setColorFilter(cardArrowColor, android.graphics.PorterDuff.Mode.SRC_IN)


        iconDrawable?.let { drawable ->
            card_header.visibility = View.VISIBLE
            card_icon.background = drawable
        }

        setInnerView(innerViewRes)

        if (startExpanded) {
            animDuration = 0
            expand()
        }

        if (expandOnClick) {
            card_layout.setOnClickListener(defaultClickListener)
            card_arrow.setOnClickListener(defaultClickListener)
        }

        if (showSwitch) {
            card_switch.setOnCheckedChangeListener(switchOnCheckedChangeListener)
        }

    }

    fun expand() {
        val initialHeight = card_layout.height
        if (!isMoving) {
            previousHeight = initialHeight
        }


        val targetHeight = getFullHeight(card_layout)

        if (targetHeight - initialHeight != 0) {
            animateViews(initialHeight,
                    targetHeight - initialHeight,
                    EXPANDING)
        }
    }

    /***
     * This function returns the actual height the layout. The getHeight() function returns the current height which might be zero if
     * the layout's visibility is GONE
     * @param layout
     * @return
     */
    private fun getFullHeight(layout: ViewGroup): Int {
        val specWidth = MeasureSpec.makeMeasureSpec(0 /* any */, MeasureSpec.UNSPECIFIED)
        val specHeight = MeasureSpec.makeMeasureSpec(0 /* any */, MeasureSpec.UNSPECIFIED)
        layout.measure(specWidth, specHeight)
        var totalHeight = 0 //layout.getMeasuredHeight();
        val initialVisibility = layout.visibility
        layout.visibility = View.VISIBLE
        val numberOfChildren = layout.childCount
        for (i in 0 until numberOfChildren) {
            val child = layout.getChildAt(i)

            val desiredWidth = MeasureSpec.makeMeasureSpec(layout.width,
                    MeasureSpec.AT_MOST)
            child.measure(desiredWidth, MeasureSpec.UNSPECIFIED)
            child.measuredHeight
            totalHeight+=child.measuredHeight
        }
        layout.visibility = initialVisibility
        return totalHeight
    }

    fun collapse() {
        val initialHeight = card_layout.measuredHeight
        if (initialHeight - previousHeight != 0) {
            animateViews(initialHeight,
                    initialHeight - previousHeight,
                    COLLAPSING)
        }
    }

    private fun animateViews(initialHeight: Int, distance: Int, animationType: Int) {

        val expandAnimation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    //Setting isExpanding/isCollapsing to false
                    isExpanding = false
                    isCollapsing = false

                    listener?.let { listener ->
                        if (animationType == EXPANDING) {
                            listener.onExpandChanged(card_layout, true)
                        } else {
                            listener.onExpandChanged(card_layout, false)
                        }
                    }
                }

                card_layout.layoutParams.height = if (animationType == EXPANDING)
                    (initialHeight + distance * interpolatedTime).toInt()
                else
                    (initialHeight - distance * interpolatedTime).toInt()
                card_container.requestLayout()

                card_container.layoutParams.height = if (animationType == EXPANDING)
                    (initialHeight + distance * interpolatedTime).toInt()
                else
                    (initialHeight - distance * interpolatedTime).toInt()

            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        val arrowAnimation = if (animationType == EXPANDING)
            RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f)
        else
            RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f)

        arrowAnimation.fillAfter = true


        arrowAnimation.duration = animDuration
        expandAnimation.duration = animDuration

        isExpanding = animationType == EXPANDING
        isCollapsing = animationType == COLLAPSING

        startAnimation(expandAnimation)
        //Log.d("SO", "Started animation: " + if (animationType == EXPANDING) "Expanding" else "Collapsing")
        card_arrow.startAnimation(arrowAnimation)
        isExpanded = animationType == EXPANDING


        if (showSwitch) {
            val switchAnimation = if (animationType == EXPANDING)
                AlphaAnimation(1f, 0f)
            else
                AlphaAnimation(0f, 1f)
            switchAnimation.duration = animDuration
            switchAnimation.fillAfter = false
            card_switch.startAnimation(switchAnimation)

            // Disable/Set clickable to prevent the toggle from working when the card is expanded
            switchAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) {
                    //
                }

                override fun onAnimationEnd(p0: Animation?) {
                    card_switch.visibility = if (animationType != EXPANDING) View.VISIBLE else View.GONE
                }

                override fun onAnimationStart(p0: Animation?) {
                    card_switch.visibility = if (animationType != EXPANDING) View.INVISIBLE else View.VISIBLE
                }
            })
        }
    }


    fun setOnExpandedListener(listener: OnExpandedListener) {
        this.listener = listener
    }

    fun setOnExpandedListener(method: (v: View?, isExpanded: Boolean) -> Unit) {
        this.listener = object : OnExpandedListener {
            override fun onExpandChanged(v: View?, isExpanded: Boolean) {
                method(v, isExpanded)
            }
        }
    }

    fun removeOnExpandedListener() {
        this.listener = null
    }


    fun setOnSwitchChangeListener(listener: OnSwitchChangeListener) {
        this.switchListener = listener
    }

    fun setOnSwitchChangeListener(method: (v: View?, isChecked: Boolean) -> Unit) {
        this.switchListener = object : OnSwitchChangeListener {
            override fun onSwitchChanged(v: View?, isChecked: Boolean) {
                method(v, isChecked)
            }

        }
    }

    fun removeOnSwitchChangeListener() {
        this.switchListener = null
    }

    fun setTitle(@StringRes titleRes: Int = -1, titleText: String = "") {
        if (titleRes != -1)
            card_title.setText(titleRes)
        else
            card_title.text = titleText
    }

    fun setIcon(@DrawableRes drawableRes: Int = -1, drawable: Drawable? = null) {
        if (drawableRes != -1) {
            iconDrawable = ContextCompat.getDrawable(context, drawableRes)
            card_icon.background = iconDrawable
        } else {
            card_icon.background = drawable
            iconDrawable = drawable
        }

    }

    fun setSwitch(boolean: Boolean){
        card_switch.isChecked = boolean
    }

    fun setSwitchEnabled(boolean: Boolean){
        card_switch.isEnabled = boolean
    }

    private fun setInnerView(resId: Int) {
        card_stub.layoutResource = resId
        innerView = card_stub.inflate()
    }


    override fun setOnClickListener(l: OnClickListener?) {
        card_arrow.setOnClickListener(l)
        super.setOnClickListener(l)
    }


    /**
     * Interfaces
     */

    interface OnExpandedListener {
        fun onExpandChanged(v: View?, isExpanded: Boolean)
    }

    interface OnSwitchChangeListener {
        fun onSwitchChanged(v: View?, isChecked: Boolean)
    }

    companion object {

        private const val DEFAULT_ANIM_DURATION = 350

        private const val COLLAPSING = 0
        private const val EXPANDING = 1
    }

}


