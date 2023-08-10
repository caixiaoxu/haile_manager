package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.ShopManagerViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.SearchType
import com.yunshang.haile_manager_android.data.entities.ShopEntity
import com.yunshang.haile_manager_android.databinding.ActivityShopManagerBinding
import com.yunshang.haile_manager_android.databinding.ItemShopListBinding
import com.yunshang.haile_manager_android.databinding.PopupShopOperateManagerBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.activity.personal.IncomeActivity
import com.yunshang.haile_manager_android.ui.view.TranslucencePopupWindow
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.NumberUtils

class ShopManagerActivity :
    BaseBusinessActivity<ActivityShopManagerBinding, ShopManagerViewModel>(
        ShopManagerViewModel::class.java,
        BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_shop_manager

    override fun backBtn(): View = mBinding.shopTitleBar.getBackBtn()

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        mBinding.shopTitleBar.getRightBtn(true).run {
            setText(R.string.operate_manager)
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.mipmap.icon_add, 0, 0, 0
            )
            compoundDrawablePadding = DimensionUtils.dip2px(this@ShopManagerActivity, 4f)
            setOnClickListener {
                showDeviceOperateView()
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 搜索界面
        mBinding.viewShopManagerSearchBg.setOnClickListener {
            startActivity(Intent(this@ShopManagerActivity, SearchActivity::class.java).apply {
                putExtra(SearchType.SearchType, SearchType.Shop)
            })
        }

        // 刷新
        mBinding.tvShopManagerListRefresh.setOnClickListener {
            mBinding.rvShopList.requestRefresh()
        }

        mBinding.rvShopList.layoutManager = LinearLayoutManager(this)
        mBinding.rvShopList.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            ).apply {
                ResourcesCompat.getDrawable(resources, R.drawable.divide_size8, null)?.let {
                    setDrawable(it)
                }
            })
        mBinding.rvShopList.adapter = CommonRecyclerAdapter<ItemShopListBinding, ShopEntity>(
            R.layout.item_shop_list,
            BR.item
        ) { mBinding, _, item ->
            var title =
                StringUtils.getString(R.string.total_earnings)
            var value =
                StringUtils.getString(R.string.unit_money) + NumberUtils.keepTwoDecimals(item.income)
            var start = title.length + 1
            var end = title.length + 1 + value.length
            // 格式化总收益样式
            mBinding?.tvItemShopTotalIncome?.text =
                com.yunshang.haile_manager_android.utils.StringUtils.formatMultiStyleStr(
                    "$title：$value",
                    arrayOf(
                        ForegroundColorSpan(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.colorPrimary,
                                null
                            )
                        ),
                        AbsoluteSizeSpan(DimensionUtils.sp2px(18f, this@ShopManagerActivity)),
//                        StyleSpan(Typeface.BOLD),
                        TypefaceSpan("money"),
                    ), start, end
                )
            mBinding?.tvItemShopTotalIncome?.setOnClickListener {
                if (true == mSharedViewModel.hasShopProfitPermission.value) {
                    //  跳转到店铺收益
                    startActivity(
                        Intent(
                            this@ShopManagerActivity,
                            IncomeActivity::class.java
                        ).apply {
                            putExtra(IncomeActivity.ProfitType, 1)
                            putExtra(IncomeActivity.ProfitSearchId, item.id)
                        })
                }
            }

            title = StringUtils.getString(R.string.device_num)
            value = item.deviceNum.toString()
            start = title.length + 1
            end = title.length + 1 + value.length
            // 格式化设备数样式
            mBinding?.tvItemShopDeviceNum?.text =
                com.yunshang.haile_manager_android.utils.StringUtils.formatMultiStyleStr(
                    "$title：$value",
                    arrayOf(
                        AbsoluteSizeSpan(DimensionUtils.sp2px(16f, this@ShopManagerActivity)),
//                        StyleSpan(Typeface.BOLD),
                        TypefaceSpan("money"),
                    ), start, end
                )

            mBinding?.tvItemShopDeviceNum?.setOnClickListener {
                startActivity(
                    Intent(
                        this@ShopManagerActivity,
                        ShopManagerActivity::class.java
                    ).apply {
                        putExtras(
                            IntentParams.DeviceManagerParams.pack(
                                SearchSelectParam(
                                    item.id,
                                    item.name
                                )
                            )
                        )
                    }
                )
            }

            // 进入详情
            mBinding?.root?.setOnClickListener {
                if (true == mSharedViewModel.hasShopInfoPermission.value) {
                    startActivity(Intent(
                        this@ShopManagerActivity,
                        ShopDetailActivity::class.java
                    ).apply {
                        putExtra(ShopDetailActivity.ShopId, item.id)
                    })
                }
            }
        }
        mBinding.rvShopList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<ShopEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out ShopEntity>?) -> Unit
                ) {
                    if (true == mSharedViewModel.hasShopListPermission.value) {
                        mViewModel.requestShopList(page, pageSize, callBack)
                    }
                }
            }
    }

    /**
     * 显示店铺管理界面
     */
    private fun AppCompatTextView.showDeviceOperateView() {
        val mPopupBinding =
            PopupShopOperateManagerBinding.inflate(LayoutInflater.from(this@ShopManagerActivity))
        val popupWindow = TranslucencePopupWindow(
            mPopupBinding.root,
            window,
            DimensionUtils.dip2px(this@ShopManagerActivity, 110f)
        )

        mPopupBinding.tvShopOperateAdd.setOnClickListener {
            popupWindow.dismiss()
            startActivity(
                Intent(
                    this@ShopManagerActivity,
                    ShopCreateAndUpdateActivity::class.java
                )
            )
        }
        mPopupBinding.tvShopOperatePaySetting.setOnClickListener {
            popupWindow.dismiss()
            startActivity(Intent(
                this@ShopManagerActivity,
                SearchSelectRadioActivity::class.java
            ).apply {
                putExtras(
                    IntentParams.SearchSelectTypeParam.pack(
                        IntentParams.SearchSelectTypeParam.SearchSelectTypePaySettingsShop,
                        mustSelect = true,
                        moreSelect = true
                    )
                )
            })
        }
        popupWindow.showAsDropDown(
            this,
            -DimensionUtils.dip2px(this@ShopManagerActivity, 16f),
            0
        )
    }

    override fun initEvent() {
        super.initEvent()

        mSharedViewModel.hasShopListPermission.observe(this) {
            mBinding.rvShopList.requestRefresh()
        }
        mSharedViewModel.hasShopInfoPermission.observe(this) {}
        mSharedViewModel.hasShopProfitPermission.observe(this) {}
        mSharedViewModel.hasShopAddPermission.observe(this) {
            if (it)
                initRightBtn()
        }

        // 修改成功后
        LiveDataBus.with(BusEvents.SHOP_LIST_STATUS)?.observe(this) {
            mBinding.rvShopList.requestRefresh()
        }
    }

    override fun initData() {
    }
}