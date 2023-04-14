package com.shangyun.haile_manager_android.ui.view.refresh

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView.*
import com.lsy.framelib.network.response.ResponseList
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
class CommonRefreshRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val mBinding: CustomRefreshRecyclerViewBinding

    // 排版布局
    var layoutManager: LayoutManager? = null
        set(value) {
            mBinding.rvRefreshList.layoutManager = value
            field = value
        }

    // 适配器
    var adapter: Adapter<*>? = null
        set(value) {
            mBinding.rvRefreshList.adapter = value
            field = value
        }

    /**
     * 分割线
     */
    fun addItemDecoration(decor: ItemDecoration) {
        mBinding.rvRefreshList.addItemDecoration(decor)
    }

    // 页数
    var page: Int = 1

    // 默认页数
    var pageSize: Int = 20

    // 请求数据
    var requestData: ((page: Int, pageSize: Int, callBack: (responseList: ResponseList<*>) -> Unit) -> Unit)? =
        null

    // 刷新数据 是否拦截后续操作
    var onRefresh: ((responseList: ResponseList<*>) -> Boolean)? = null

    // 加载数据 是否拦截后续操作
    var onLoadMore: ((responseList: ResponseList<*>) -> Boolean)? = null

    init {
        mBinding = CustomRefreshRecyclerViewBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.custom_refresh_recycler_view, null, false)
        )
        addView(mBinding.root)

        // 刷新
        mBinding.refreshLayout.setOnRefreshListener {
            requestData?.invoke(page.also { page = 1 }, pageSize) {
                onRefresh(it)
            }
        }

        // 加载
        mBinding.refreshLayout.setOnLoadMoreListener {
            requestData?.invoke(page, pageSize) {
                onLoadMore(it)
            }
        }
    }

    /**
     * 刷新数据
     */
    private fun onRefresh(it: ResponseList<*>) {
        //判断 当前页 数量不为0，页数加1
        if (0 < it.pageSize) {
            page++
        }

        // 自定义处理
        if (true == onRefresh?.invoke(it)) {
            return
        }

        // 判断列表数量 >= 总数据数量
        if ((adapter?.itemCount ?: 0) >= it.total) {
            mBinding.refreshLayout.setEnableLoadMore(false)
        }
    }

    /**
     * 刷新数据
     */
    private fun onLoadMore(it: ResponseList<*>) {
        //判断 当前页 数量不为0，页数加1
        if (0 < it.pageSize) {
            page++
        }

        // 自定义处理
        if (true == onLoadMore?.invoke(it)) {
            return
        }

        // 判断列表数量 >= 总数据数量
        if ((adapter?.itemCount ?: 0) >= it.total) {
            mBinding.refreshLayout.setEnableLoadMore(false)
        }
    }
}