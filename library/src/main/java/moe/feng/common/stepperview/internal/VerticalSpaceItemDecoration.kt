package moe.feng.common.stepperview.internal

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.State

/**
 * @hide
 */
class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) : ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
        outRect.bottom = verticalSpaceHeight
    }
}