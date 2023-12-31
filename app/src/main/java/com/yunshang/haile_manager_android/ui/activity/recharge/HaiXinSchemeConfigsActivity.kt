package com.yunshang.haile_manager_android.ui.activity.recharge

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.HaiXinSchemeConfigsViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.HaixinSchemeConfigEntity
import com.yunshang.haile_manager_android.databinding.ActivityHaixinSchemeConfigsBinding
import com.yunshang.haile_manager_android.databinding.ItemHaixinSchemeConfigsBinding
import com.yunshang.haile_manager_android.databinding.PopupDeviceOperateManagerBinding
import com.yunshang.haile_manager_android.databinding.PopupRechargeOperateManagerBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.device.DeviceBatchStartActivity
import com.yunshang.haile_manager_android.ui.activity.device.DeviceBatchUpdateActivity
import com.yunshang.haile_manager_android.ui.activity.device.DeviceCreateV2Activity
import com.yunshang.haile_manager_android.ui.view.TranslucencePopupWindow
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.UserPermissionUtils

class HaiXinSchemeConfigsActivity :
    BaseBusinessActivity<ActivityHaixinSchemeConfigsBinding, HaiXinSchemeConfigsViewModel>(
        HaiXinSchemeConfigsViewModel::class.java
    ) {

    override fun layoutId(): Int = R.layout.activity_haixin_scheme_configs

    override fun backBtn(): View = mBinding.barHaixinSchemeConfigsTitle.getBackBtn()

    private val adapter by lazy {
        CommonRecyclerAdapter<ItemHaixinSchemeConfigsBinding, HaixinSchemeConfigEntity>(
            R.layout.item_haixin_scheme_configs, BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.root?.setOnClickListener {
                if (!UserPermissionUtils.hasVipDetailPermission()) return@setOnClickListener
                startActivity(
                    Intent(
                        this@HaiXinSchemeConfigsActivity,
                        HaiXinSchemeConfigsDetailActivity::class.java
                    ).apply {
                        putExtras(IntentParams.ShopParams.pack(item.shopId, item.shopName))
                    })
            }
        }
    }

    override fun initEvent() {
        super.initEvent()
        // 监听列表变化
        LiveDataBus.with(BusEvents.HAIXIN_SCHEME_LIST_STATUS)?.observe(this) {
            mBinding.rvHaixinSchemeConfigsList.requestRefresh()
        }
        // 监听删除
        LiveDataBus.with(BusEvents.HAIXIN_SCHEME_LIST_ITEM_DELETE_STATUS, Int::class.java)
            ?.observe(this) {
                adapter.deleteItem { item -> item.id == it }
            }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        if (UserPermissionUtils.hasVipAddPermission()) {
            mBinding.barHaixinSchemeConfigsTitle.getRightBtn(true).run {
                setText(R.string.operate_manager)
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.mipmap.icon_add, 0, 0, 0
                )
                compoundDrawablePadding =
                    DimensionUtils.dip2px(this@HaiXinSchemeConfigsActivity, 4f)
                setOnClickListener {
                    showDeviceOperateView()
                }
            }
        }
        mBinding.rvHaixinSchemeConfigsList.layoutManager = LinearLayoutManager(this)
        ResourcesCompat.getDrawable(resources, R.drawable.divder_efefef, null)?.let {
            mBinding.rvHaixinSchemeConfigsList.addItemDecoration(
                DividerItemDecoration(
                    this@HaiXinSchemeConfigsActivity,
                    DividerItemDecoration.VERTICAL
                ).apply {
                    setDrawable(it)
                })
        }
        mBinding.rvHaixinSchemeConfigsList.adapter = adapter
        mBinding.rvHaixinSchemeConfigsList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<HaixinSchemeConfigEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out HaixinSchemeConfigEntity>?) -> Unit
                ) {
                    mViewModel.requestSchemeList(page, pageSize, callBack)
                }
            }
    }

    /**
     * 显示充值管理界面
     */
    private fun AppCompatTextView.showDeviceOperateView() {
        val mPopupBinding =
            PopupRechargeOperateManagerBinding.inflate(LayoutInflater.from(this@HaiXinSchemeConfigsActivity))
        val popupWindow = TranslucencePopupWindow(
            mPopupBinding.root,
            window,
            DimensionUtils.dip2px(this@HaiXinSchemeConfigsActivity, 110f)
        )

        mPopupBinding.tvRechargeOperateAdd.setOnClickListener {
            popupWindow.dismiss()
            startActivity(
                Intent(
                    this@HaiXinSchemeConfigsActivity,
                    HaiXinSchemeConfigsCreateActivity::class.java
                )
            )
        }
        mPopupBinding.tvRechargeOperateUpdate.setOnClickListener {
            popupWindow.dismiss()
            startActivity(
                Intent(
                    this@HaiXinSchemeConfigsActivity,
                    HaiXinSchemeConfigsCreateActivity::class.java
                ).apply {
                    putExtras(IntentParams.HaiXinSchemeConfigsCreateParams.pack(true))
                }
            )
        }
        popupWindow.showAsDropDown(
            this,
            -DimensionUtils.dip2px(this@HaiXinSchemeConfigsActivity, 16f),
            0
        )
    }

    override fun initData() {
        mBinding.rvHaixinSchemeConfigsList.requestRefresh()
    }
}