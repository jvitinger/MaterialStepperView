package moe.feng.common.stepperview

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import kotlinx.android.synthetic.main.vertical_stepper_item_view_layout.view.*
import moe.feng.common.stepperview.VerticalStepperItemView.State.STATE_DONE
import moe.feng.common.stepperview.VerticalStepperItemView.State.STATE_NORMAL

private const val PROPERTY_BACKGROUND = "backgroundColor"

class VerticalStepperItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr) {

    /**
     * Internal Views
     */
    private lateinit var pointBackground: View
    private lateinit var verticalLine: View
    private lateinit var pointNumber: TextView
    private lateinit var titleText: TextView
    private lateinit var customView: FrameLayout
    private lateinit var pointFrame: FrameLayout
    private lateinit var rightContainer: LinearLayout
    private lateinit var doneIconView: ImageView
    private lateinit var marginBottomView: View

    private var pointColorAnimator: ValueAnimator? = null

    private val oneDpInPixels = resources.getDimensionPixelSize(R.dimen.dp1)

    private lateinit var parentStepperView: VerticalStepperView

    init {
        val lp: ViewGroup.LayoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams = lp
    }

    fun init(parentStepperView: VerticalStepperView) {
        this.parentStepperView = parentStepperView
        prepareViews(context)
    }

    override fun addView(child: View, index: Int, layoutParams: ViewGroup.LayoutParams) {
        if (child.id == R.id.vertical_stepper_item_view_layout) {
            super.addView(child, index, layoutParams)
        } else {
            customView.addView(child, index, layoutParams)
        }
    }

    private fun prepareViews(context: Context) {
        // Inflate and find views
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val inflateView: View = inflater.inflate(R.layout.vertical_stepper_item_view_layout, null)
        pointBackground = inflateView.stepper_point_background
        verticalLine = inflateView.stepper_line
        pointNumber = inflateView.stepper_number
        titleText = inflateView.stepper_title
        customView = inflateView.stepper_custom_view
        pointFrame = inflateView.stepper_point_frame
        rightContainer = inflateView.stepper_right_layout
        doneIconView = inflateView.stepper_done_icon
        marginBottomView = inflateView.stepper_margin_bottom

        verticalLine.setBackgroundColor(parentStepperView.lineColor)
        doneIconView.setImageDrawable(parentStepperView.doneIcon)

        if (parentStepperView.isAnimationEnabled) {
            rightContainer.layoutTransition = LayoutTransition()
        } else {
            rightContainer.layoutTransition = null
        }

        // Add view
        val lp = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(inflateView, lp)

        // Set title top margin
        titleText.viewTreeObserver.addOnGlobalLayoutListener {
            val singleLineHeight = titleText.measuredHeight
            val topMargin = (pointFrame.measuredHeight - singleLineHeight) / 2

            // Only update top margin when it is positive, preventing titles being truncated.
            if (topMargin > 0) {
                (titleText.layoutParams as MarginLayoutParams).topMargin = topMargin
            }
        }
    }

    private fun updateMarginBottom() {
        marginBottomView.layoutParams.height = (if (!isLastStep) if (state != State.STATE_SELECTED) 28 else 36 else 12) * oneDpInPixels
    }

    @set:Synchronized
    var state: State = STATE_NORMAL
        set(state) {

            val normalColor = parentStepperView.normalColor
            val activatedColor = parentStepperView.activatedColor
            val duration = parentStepperView.animationDuration

            pointColorAnimator?.cancel()
            if (state != STATE_NORMAL && field == STATE_NORMAL) {
                pointColorAnimator = ObjectAnimator.ofArgb(pointBackground, PROPERTY_BACKGROUND, normalColor, activatedColor).apply {
                    setDuration(duration)
                    start()
                }
            } else if (state == STATE_NORMAL && field != STATE_NORMAL) {
                pointColorAnimator = ObjectAnimator.ofArgb(pointBackground, PROPERTY_BACKGROUND, activatedColor, normalColor).apply {
                    setDuration(duration)
                    start()
                }
            } else {
                pointBackground.setBackgroundColor(if (state == STATE_NORMAL) normalColor else activatedColor)
            }

            if (state == STATE_DONE && field != STATE_DONE) {
                doneIconView.animate().alpha(1f).setDuration(duration).start()
                pointNumber.animate().alpha(0f).setDuration(duration).start()
            } else if (state != STATE_DONE && field == STATE_DONE) {
                doneIconView.animate().alpha(0f).setDuration(duration).start()
                pointNumber.animate().alpha(1f).setDuration(duration).start()
            } else {
                doneIconView.alpha = if (state == STATE_DONE) 1f else 0f
                pointNumber.alpha = if (state == STATE_DONE) 0f else 1f
            }

            TextViewCompat.setTextAppearance(titleText, when (state) {
                STATE_DONE -> R.style.TextAppearance_Widget_Stepper_Done
                STATE_NORMAL -> R.style.TextAppearance_Widget_Stepper_Normal
                else -> R.style.TextAppearance_Widget_Stepper_Selected
            })

            field = state
            updateMarginBottom()
        }

    var title: CharSequence = ""
        set(title) {
            titleText.text = title
            field = title
        }

    var index: Int = 1
        set(index) {
            field = index
            pointNumber.text = index.toString()
        }

    var isLastStep = false
        set(isLastStep) {
            field = isLastStep
            verticalLine.visibility = if (isLastStep) View.INVISIBLE else View.VISIBLE
            updateMarginBottom()
        }

    fun canPrevStep(): Boolean {
        return parentStepperView.canPrev()
    }

    fun prevStep(): Boolean {
        return parentStepperView.prevStep()
    }

    fun canNextStep(): Boolean {
        return parentStepperView.canNext()
    }

    fun nextStep(): Boolean {
        return parentStepperView.nextStep()
    }

    enum class State {
        STATE_NORMAL,
        STATE_SELECTED,
        STATE_DONE
    }
}