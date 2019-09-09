package moe.feng.common.stepperview.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import moe.feng.common.stepperview.VerticalStepperView

class VerticalStepperDemoFragment : androidx.fragment.app.Fragment() {

    private lateinit var mVerticalStepperView: VerticalStepperView

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vertical_stepper_demo, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mVerticalStepperView = view.findViewById(R.id.vertical_stepper_view)
        mVerticalStepperView.steps = listOf(
                MyDemoStep("First title"),
                MyDemoStep("Second title"),
                MyDemoStep("Third title"),
                MyDemoStep("Forth title")
        )
    }
}
