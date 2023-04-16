package com.shangyun.haile_manager_android.ui.view.refresh

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.databinding.CustomRefreshRecyclerViewBinding
import com.shangyun.haile_manager_android.ui.adapter.CommonRecyclerAdapter

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
class CommonRefreshRecyclerView<D> @JvmOverloads constructor(
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
    var adapter: CommonRecyclerAdapter<*, D>? = null
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
    var requestData: OnRequestDataListener<D>? = null

    init {
        mBinding = CustomRefreshRecyclerViewBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.custom_refresh_recycler_view, null, false)
        )
        addView(mBinding.root)

        // 刷新
        mBinding.refreshLayout.setOnRefreshListener {
            requestData?.requestData(page.also { page = 1 }, pageSize) {
                onRefresh(it)
            }
        }

        // 加载
        mBinding.refreshLayout.setOnLoadMoreListener {
            requestData?.requestData(page, pageSize) {
                onLoadMore(it)
            }
        }
        // 自动刷新
        mBinding.refreshLayout.autoRefresh()
    }

    /**
     * 刷新数据
     */
    private fun onRefresh(it: ResponseList<D>) {
        mBinding.refreshLayout.finishRefresh()
        refreshDate(it, true)
    }

    /**
     * 加载数据
     */
    private fun onLoadMore(it: ResponseList<D>) {
        mBinding.refreshLayout.finishLoadMore()
        refreshDate(it)
    }

    /**
     * 刷新数据
     * @param it 数据列表
     * @param isRefresh 是否刷新
     */
    private fun refreshDate(it: ResponseList<D>, isRefresh: Boolean = false) {
        //判断 当前页 数量不为0，页数加1
        if (0 < it.pageSize) {
            page++
        }

        // 自定义处理
        if (true == if (isRefresh) requestData?.onRefresh(it) else requestData?.onLoadMore(it)) {
            return
        }
        // 刷新数据
        adapter?.refreshList(it.items, isRefresh)

        // 判断列表数量 >= 总数据数量
        if ((adapter?.itemCount ?: 0) >= it.total) {
            mBinding.refreshLayout.setEnableLoadMore(false)
        }
    }

    /**
     * 请求监听
     */
    abstract class OnRequestDataListener<D> {
        /**
         * 请示数据
         * @param page 页数
         * @param pageSize 请求数量
         * @param callBack 请求回调
         */
        abstract fun requestData(
            page: Int,
            pageSize: Int,
            callBack: (responseList: ResponseList<D>) -> Unit
        )

        /**
         * 刷新数据
         * @param responseList 返回的列表数据
         * @return 是否拦截后续操作
         */
        fun onRefresh(responseList: ResponseList<D>): Boolean = false

        /**
         * 加载数据
         * @param responseList 返回的列表数据
         * @return 是否拦截后续操作
         */
        fun onLoadMore(responseList: ResponseList<D>): Boolean = false
    }
}