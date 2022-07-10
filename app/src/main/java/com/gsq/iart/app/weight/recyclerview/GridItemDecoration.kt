package com.gsq.iart.app.weight.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridItemDecoration(
    private var spanCount: Int,
    private var spacing: Int,
    private var includeEdge: Boolean,
    private var hasTopBottomSpace: Boolean = true
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount
        if (includeEdge) {//是否有左右边距
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount
            if(hasTopBottomSpace) {
                if (position < spanCount) {
                    outRect.top = spacing
                }
                outRect.bottom = spacing
            }
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if(hasTopBottomSpace) {
                if (position < spanCount) {
                    outRect.top = spacing
                }
                outRect.bottom = spacing
            }
        }
    }

}