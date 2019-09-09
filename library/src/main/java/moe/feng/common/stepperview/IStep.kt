package moe.feng.common.stepperview

import android.content.Context
import android.view.View

interface IStep {

    fun createCustomView(context: Context, parentView: VerticalStepperItemView): View

    fun setState(state: VerticalStepperItemView.State)

    fun getTitle(): String
}