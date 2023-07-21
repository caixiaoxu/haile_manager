package com.yunshang.haile_manager_android.ui.activity.notice

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.NoticeDetailViewModel
import com.yunshang.haile_manager_android.business.vm.NoticeManagerViewModel
import com.yunshang.haile_manager_android.data.entities.NoticeEntity
import com.yunshang.haile_manager_android.databinding.ActivityNoticeManagerBinding
import com.yunshang.haile_manager_android.databinding.ItemNoticeManagerBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView

class NoticeManagerActivity :
    BaseBusinessActivity<ActivityNoticeManagerBinding, NoticeManagerViewModel>(
        NoticeManagerViewModel::class.java
    ) {

    override fun layoutId(): Int = R.layout.activity_notice_manager

    override fun backBtn(): View = mBinding.barNoticeManagerTitle.getBackBtn()

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemNoticeManagerBinding, NoticeEntity>(
            R.layout.item_notice_manager, BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.root?.setOnClickListener {
                startActivity(
                    Intent(
                        this@NoticeManagerActivity,
                        NoticeDetailActivity::class.java
                    ).apply {
                        putExtra(NoticeDetailActivity.NoticeId, item.id)
                    })
            }
        }
    }

    override fun initEvent() {
        super.initEvent()

        mSharedViewModel.hasPersonAddPermission.observe(this) {
            if (it) initRightBtn()
        }
        mSharedViewModel.hasPersonListPermission.observe(this) {
            if (it) mBinding.rvNoticeManagerList.requestRefresh()
        }
        mSharedViewModel.hasPersonInfoPermission.observe(this) {}

        // 新增成功
        LiveDataBus.with(BusEvents.NOTICE_LIST_STATUS)?.observe(this) {
            mBinding.rvNoticeManagerList.requestRefresh()
        }
        // 删除成功
        LiveDataBus.with(BusEvents.NOTICE_LIST_ITEM_DELETE_STATUS)?.observe(this) {
            mAdapter.deleteItem { item -> item.id == it }
        }
    }

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        mBinding.barNoticeManagerTitle.getRightBtn(true).run {
            setText(R.string.add_notice)
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.mipmap.icon_add, 0, 0, 0
            )
            compoundDrawablePadding = DimensionUtils.dip2px(this@NoticeManagerActivity, 4f)
            setOnClickListener {
                startActivity(
                    Intent(
                        this@NoticeManagerActivity,
                        NoticeCreateActivity::class.java
                    )
                )
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.rvNoticeManagerList.layoutManager = LinearLayoutManager(this)
        ContextCompat.getDrawable(
            this,
            R.drawable.shape_sf7f7f7_wh8
        )?.let {
            mBinding.rvNoticeManagerList.addItemDecoration(
                DividerItemDecoration(
                    this,
                    DividerItemDecoration.VERTICAL
                ).apply { setDrawable(it) })
        }
        mBinding.rvNoticeManagerList.adapter = mAdapter
        mBinding.rvNoticeManagerList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<NoticeEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out NoticeEntity>?) -> Unit
                ) {
                    if (true == mSharedViewModel.hasPersonListPermission.value) {
                        mViewModel.requestNoticeList(page, pageSize, callBack)
                    }
                }
            }
    }

    override fun initData() {
    }
}