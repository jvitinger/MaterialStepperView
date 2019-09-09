package moe.feng.common.stepperview.internal

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * @hide
 */
class ClipOvalFrameLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr) {

    init {
        clipToOutline = true
    }
}