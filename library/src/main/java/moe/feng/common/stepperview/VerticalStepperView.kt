package moe.feng.common.stepperview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import moe.feng.common.stepperview.R.*
import moe.feng.common.stepperview.internal.VerticalSpaceItemDecoration
import kotlin.math.abs
import kotlin.math.min

class VerticalStepperView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter

    var isAnimationEnabled = false
    var animationDuration = 0L
    lateinit var doneIcon: Drawable

    var steps: List<IStep> = emptyList()
        set(steps) {
            field = steps
            adapter.notifyDataSetChanged()
        }

    var currentStep: Int = 0
        set(currentStep) {
            val minIndex = min(currentStep, field)
            val count = abs(currentStep - field) + 1
            field = currentStep
            if (isAnimationEnabled) {
                adapter.notifyItemRangeChanged(minIndex, count)
            } else {
                adapter.notifyDataSetChanged()
            }
        }

    var normalColor = 0
        set(color) {
            field = color
            adapter.notifyDataSetChanged()
        }

    var activatedColor = 0
        set(color) {
            field = color
            adapter.notifyDataSetChanged()
        }

    var lineColor = 0

    init {
        prepareRecyclerView(context)
        if (attrs != null) {
            val a: TypedArray = context.obtainStyledAttributes(attrs, styleable.VerticalStepperView,
                    defStyleAttr, style.Widget_Stepper)
            normalColor = a.getColor(styleable.VerticalStepperView_step_normal_color, normalColor)
            activatedColor = a.getColor(styleable.VerticalStepperView_step_activated_color, activatedColor)
            animationDuration = a.getInt(styleable.VerticalStepperView_step_animation_duration, animationDuration.toInt()).toLong()
            isAnimationEnabled = a.getBoolean(styleable.VerticalStepperView_step_enable_animation, true)
            lineColor = a.getColor(styleable.VerticalStepperView_step_line_color, lineColor)

            if (a.hasValue(styleable.VerticalStepperView_step_done_icon)) {
                doneIcon = a.getDrawable(styleable.VerticalStepperView_step_done_icon)!!
            }
            a.recycle()
        }
    }

    private fun prepareRecyclerView(context: Context) {
        recyclerView = RecyclerView(context)
        adapter = ItemAdapter()

        recyclerView.clipToPadding = false
        recyclerView.setPadding(0, resources.getDimensionPixelSize(dimen.stepper_margin_top), 0, 0)
        recyclerView.addItemDecoration(VerticalSpaceItemDecoration(resources.getDimensionPixelSize(dimen.vertical_stepper_item_space_height)))
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        addView(recyclerView, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

    fun canNext(): Boolean {
        return currentStep < steps.size - 1
    }

    fun canPrev(): Boolean {
        return currentStep > 0
    }

    fun nextStep(): Boolean {
        if (canNext()) {
            currentStep++
            return true
        }
        return false
    }

    fun prevStep(): Boolean {
        if (canPrev()) {
            currentStep--
            return true
        }
        return false
    }

    /**
     * Internal RecyclerView Adapter to show item views.
     */
    internal inner class ItemAdapter : Adapter<ItemAdapter.ItemHolder>() {

        /**
         * Ensure each position of the view will have its own type.
         * It basically switches the recycling off.
         */
        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun getItemCount(): Int {
            return steps.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, position: Int): ItemHolder {
            val verticalStepperItemView = VerticalStepperItemView(parent.context)
            verticalStepperItemView.init(this@VerticalStepperView)
            verticalStepperItemView.addView(steps[position].createCustomView(context, verticalStepperItemView))
            return ItemHolder(verticalStepperItemView)
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            holder.mItemView.index = position + 1
            holder.mItemView.isLastStep = position == itemCount - 1
            holder.mItemView.title = steps[position].getTitle()

            when {
                currentStep > position -> holder.mItemView.state = VerticalStepperItemView.State.STATE_DONE
                currentStep < position -> holder.mItemView.state = VerticalStepperItemView.State.STATE_NORMAL
                else -> holder.mItemView.state = VerticalStepperItemView.State.STATE_SELECTED
            }

            steps[position].setState(holder.mItemView.state)
        }

        internal inner class ItemHolder(var mItemView: VerticalStepperItemView) : ViewHolder(mItemView)
    }
}