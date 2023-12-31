package com.yunshang.haile_manager_android.ui.view.refresh

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/14 11:35
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
</desc></version></time></author> */
class GridSpaceItemDecoration(private val spaceH: Int, private val spaceV: Int) :
    ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.layoutManager is GridLayoutManager) {
            (parent.layoutManager as GridLayoutManager).spanCount.let { spanCount ->
                // 横行间隔分成三份
                val position = parent.getChildAdapterPosition(view)
                val l1 = spaceH / spanCount

                //由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
                if (position % spanCount == 0) {
                    outRect.right = 2 * l1
                } else if (position % spanCount == spanCount - 1) {
                    outRect.left = 2 * l1
                } else {
                    outRect.left = l1
                    outRect.right = l1
                }
                outRect.top = spaceV
                // 第一行置空
                if (parent.getChildLayoutPosition(view) < spanCount) {
                    outRect.top = 0
                }
            }
        }
    }
}