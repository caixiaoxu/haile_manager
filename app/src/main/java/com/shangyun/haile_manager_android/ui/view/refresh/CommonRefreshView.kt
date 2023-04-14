package com.shangyun.haile_manager_android.ui.view.refresh

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.databinding.CustomRefreshRecyclerViewBinding

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/14 16:29
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class CommonRefreshView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : SmartRefreshLayout(context, attrs) {

    init {
        // 刷新样式
        setRefreshHeader(ClassicsHeader(context))
        // 不可加载
        setEnableLoadMore(false)
        // 刷新
        setOnRefreshListener { onRefresh() }
    }

    // 请求数据
    var requestData: (() -> Unit)? = null

    /**
     * 刷新
     */
    private fun onRefresh() {
        requestData?.invoke()
    }
}