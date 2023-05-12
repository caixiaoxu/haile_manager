package com.shangyun.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.event.BusEvents
import com.shangyun.haile_manager_android.business.vm.ShopManagerViewModel
import com.shangyun.haile_manager_android.data.arguments.SearchType
import com.shangyun.haile_manager_android.data.entities.ShopEntity
import com.shangyun.haile_manager_android.databinding.ActivityShopManagerBinding
import com.shangyun.haile_manager_android.databinding.ItemShopListBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.activity.common.SearchActivity
import com.shangyun.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.shangyun.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.shangyun.haile_manager_android.utils.NumberUtils

class ShopManagerActivity :
    BaseBusinessActivity<ActivityShopManagerBinding, ShopManagerViewModel>() {
    private val mShopManagerViewModel by lazy {
        getActivityViewModelProvider(this)[ShopManagerViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_shop_manager

    override fun getVM(): ShopManagerViewModel = mShopManagerViewModel

    override fun backBtn(): View = mBinding.shopTitleBar.getBackBtn()

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        mBinding.shopTitleBar.getRightBtn(true).run {
            setText(R.string.add_shop)
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.mipmap.icon_add, 0, 0, 0
            )
            compoundDrawablePadding = DimensionUtils.dip2px(this@ShopManagerActivity, 4f)
            setOnClickListener {
                startActivity(
                    Intent(
                        this@ShopManagerActivity,
                        ShopCreateAndUpdateActivity::class.java
                    )
                )
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 搜索界面
        mBinding.viewShopManagerSearchBg.setOnClickListener {
            startActivity(Intent(this@ShopManagerActivity, SearchActivity::class.java).apply {
                putExtra(SearchType.SearchType,SearchType.Shop)
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
                ResourcesCompat.getDrawable(resources, R.drawable.shape_height_8, null)?.let {
                    setDrawable(it)
                }
            })
        mBinding.rvShopList.adapter = CommonRecyclerAdapter<ItemShopListBinding, ShopEntity>(
            R.layout.item_shop_list,
            BR.item
        ) { mBinding, _, item ->
            mBinding?.item = item

            var title = StringUtils.getString(R.string.total_income)
            var value =
                NumberUtils.keepTwoDecimals(item.income) + StringUtils.getString(R.string.unit_yuan)
            var start = title.length + 1
            var end = title.length + 1 + value.length
            // 格式化总收益样式
            mBinding?.tvItemShopTotalIncome?.text =
                com.shangyun.haile_manager_android.utils.StringUtils.formatMultiStyleStr(
                    "$title：$value",
                    arrayOf(
                        ForegroundColorSpan(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.colorPrimary,
                                null
                            )
                        ),
                        AbsoluteSizeSpan(DimensionUtils.sp2px(this@ShopManagerActivity, 18f)),
                        StyleSpan(Typeface.BOLD),
                        TypefaceSpan("money")
                    ), start, end
                )
            mBinding?.tvItemShopTotalIncome?.setOnClickListener {
                if (true == mSharedViewModel.hasShopProfitPermission.value) {
                    // TODO 跳转到店铺收益
                }
            }

            title = StringUtils.getString(R.string.device_num)
            value = item.deviceNum.toString() + StringUtils.getString(R.string.unit_tai)
            start = title.length + 1
            end = title.length + 1 + value.length
            // 格式化设备数样式
            mBinding?.tvItemShopDeviceNum?.text =
                com.shangyun.haile_manager_android.utils.StringUtils.formatMultiStyleStr(
                    "$title：$value",
                    arrayOf(
                        AbsoluteSizeSpan(DimensionUtils.sp2px(this@ShopManagerActivity, 16f)),
                        StyleSpan(Typeface.BOLD),
                    ), start, end
                )

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
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out ShopEntity>?) -> Unit
                ) {
                    if (true == mSharedViewModel.hasShopListPermission.value) {
                        mShopManagerViewModel.requestShopList(page, pageSize, callBack)
                    }
                }
            }
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
        mBinding.vm = mShopManagerViewModel
    }
}