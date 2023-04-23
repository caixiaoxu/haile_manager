package com.shangyun.haile_manager_android.ui.activity.device

import android.content.Context
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
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.DeviceManagerViewModel
import com.shangyun.haile_manager_android.data.entities.DeviceEntity
import com.shangyun.haile_manager_android.databinding.ActivityDeviceManagerBinding
import com.shangyun.haile_manager_android.databinding.ItemDeviceListBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.shangyun.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.shangyun.haile_manager_android.utils.NumberUtils
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView


class DeviceManagerActivity :
    BaseBusinessActivity<ActivityDeviceManagerBinding, DeviceManagerViewModel>() {

    private val mDeviceManagerViewModel by lazy {
        getActivityViewModelProvider(this)[DeviceManagerViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_device_manager


    override fun getVM(): DeviceManagerViewModel = mDeviceManagerViewModel

    override fun backBtn(): View = mBinding.barDeviceTitle.getBackBtn()

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        mBinding.barDeviceTitle.getRightArea()
            .addView(
                Button(this).apply {
                    setText(R.string.add_device)
                    setTextColor(Color.WHITE)
                    textSize = 14f
                    setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.mipmap.icon_add, 0, 0, 0
                    )
                    compoundDrawablePadding = DimensionUtils.dip2px(this@DeviceManagerActivity, 4f)
                    val pH = DimensionUtils.dip2px(this@DeviceManagerActivity, 12f)
                    val pV = DimensionUtils.dip2px(this@DeviceManagerActivity, 4f)
                    setPadding(pH, pV, pH, pV)
                    gravity = Gravity.CENTER
                    setBackgroundResource(R.drawable.shape_sf0a258_r22)
                    setOnClickListener {
                        //TODO 创建
                    }
                },
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    DimensionUtils.dip2px(this@DeviceManagerActivity, 28f)
                ).apply {
                    setMargins(0, 0, DimensionUtils.dip2px(this@DeviceManagerActivity, 16f), 0)
                }
            )
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 刷新
        mBinding.tvDeviceManagerListRefresh.setOnClickListener {
            mBinding.rvDeviceManagerList.requestRefresh()
        }

        mBinding.rvDeviceManagerList.layoutManager = LinearLayoutManager(this)
        mBinding.rvDeviceManagerList.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            ).apply {
                ResourcesCompat.getDrawable(resources, R.drawable.shape_height_8, null)?.let {
                    setDrawable(it)
                }
            })
        mBinding.rvDeviceManagerList.adapter =
            CommonRecyclerAdapter<ItemDeviceListBinding, DeviceEntity>(
                R.layout.item_device_list,
                BR.item
            ) { mBinding, _, item ->
                mBinding?.item = item

                val title = StringUtils.getString(R.string.total_income)
                val value =
                    NumberUtils.keepTwoDecimals(item.income) + StringUtils.getString(R.string.unit_yuan)
                val start = title.length + 1
                val end = title.length + 1 + value.length
                // 格式化总收益样式
                mBinding?.tvItemDeviceTotalIncome?.text =
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
                            AbsoluteSizeSpan(DimensionUtils.sp2px(this@DeviceManagerActivity, 18f)),
                            StyleSpan(Typeface.BOLD),
                            TypefaceSpan("money")
                        ), start, end
                    )
                mBinding?.tvItemDeviceTotalIncome?.setOnClickListener {
                    if (true == mSharedViewModel.hasDeviceProfitPermission.value) {
                        // TODO 跳转到设备收益
                    }
                }

                // 进入详情
                mBinding?.root?.setOnClickListener {
                    if (true == mSharedViewModel.hasDeviceInfoPermission.value) {
                        //TODO 设备详情
                    }
                }
            }
        mBinding.rvDeviceManagerList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<DeviceEntity>() {
                override fun requestData(
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<DeviceEntity>?) -> Unit
                ) {
                    if (true == mSharedViewModel.hasDeviceListPermission.value) {
                        mDeviceManagerViewModel.requestDeviceList(page, pageSize, callBack)
                    }
                }
            }
    }

    override fun initEvent() {
        super.initEvent()
        mSharedViewModel.hasDeviceListPermission.observe(this) {}
        mSharedViewModel.hasDeviceInfoPermission.observe(this) {}
        mSharedViewModel.hasDeviceProfitPermission.observe(this) {}
        mSharedViewModel.hasDeviceAddPermission.observe(this) {
            if (it)
                initRightBtn()
        }

        // 刷新状态
        mDeviceManagerViewModel.deviceStatus.observe(this) { list ->
            mBinding.indicatorDeviceStatus.navigator = CommonNavigator(this).apply {

                adapter = object : CommonNavigatorAdapter() {
                    override fun getCount(): Int = list.size

                    override fun getTitleView(context: Context?, index: Int): IPagerTitleView? {
                        return SimplePagerTitleView(context).apply {
                            normalColor = Color.parseColor("#666666")
                            selectedColor = Color.WHITE
                            list[index].run {
                                text = title + if (0 < num) num else ""
                                setOnClickListener {
                                    mDeviceManagerViewModel.curWorkStatus.value = value
                                    onPageSelected(index)
                                    notifyDataSetChanged()
                                }
                            }
                        }
                    }

                    override fun getIndicator(context: Context?): IPagerIndicator? {
                        return WrapPagerIndicator(context).apply {
                            verticalPadding = DimensionUtils.dip2px(this@DeviceManagerActivity, 4f)
                            fillColor = ContextCompat.getColor(
                                this@DeviceManagerActivity,
                                R.color.colorPrimary
                            )
                            roundRadius = DimensionUtils.dip2px(this@DeviceManagerActivity,14f).toFloat()
                        }
                    }
                }
            }
        }

        // 切换工作状态
        mDeviceManagerViewModel.curWorkStatus.observe(this){
            mBinding.rvDeviceManagerList.requestRefresh()
        }
    }

    override fun initData() {
        mBinding.vm = mDeviceManagerViewModel

        mDeviceManagerViewModel.requestDeviceStatusTotals()
    }
}