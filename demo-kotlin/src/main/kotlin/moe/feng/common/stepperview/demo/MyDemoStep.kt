package moe.feng.common.stepperview.demo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import moe.feng.common.stepperview.IStep
import moe.feng.common.stepperview.VerticalStepperItemView

class MyDemoStep(private val title: String) : IStep {

    private lateinit var textView: TextView
    private lateinit var nextButton: Button
    private lateinit var prevButton: Button

    override fun getTitle() = title

    override fun createCustomView(context: Context, parentView: VerticalStepperItemView): View {
        Log.d("VerticalStepperDemo", "createCustomView()")
        val inflateView = LayoutInflater.from(context).inflate(R.layout.vertical_stepper_sample_item, parentView, false) as ViewGroup
        textView = inflateView.findViewById(R.id.item_content)
        textView.text = getTitle()

        nextButton = inflateView.findViewById(R.id.button_next)
        nextButton.text = "Next"
        nextButton.setOnClickListener {
            if (parentView.nextStep()) {
                Snackbar.make(parentView, "Next!", Snackbar.LENGTH_LONG).show()
            }
        }

        prevButton = inflateView.findViewById(R.id.button_prev)
        prevButton.text = "Previous"
        inflateView.findViewById<View>(R.id.button_prev).setOnClickListener {
            if (parentView.prevStep()) {
                Snackbar.make(parentView, "Previous!", Snackbar.LENGTH_LONG).show()
            }
        }
        return inflateView
    }

    override fun setState(state: VerticalStepperItemView.State) {
        Log.d("VerticalStepperDemo", "setState($state)")
        textView.text = state.toString()
        if (state == VerticalStepperItemView.State.STATE_SELECTED) {
            nextButton.visibility = View.VISIBLE
            prevButton.visibility = View.VISIBLE
        } else {
            nextButton.visibility = View.GONE
            prevButton.visibility = View.GONE
        }
    }
}
