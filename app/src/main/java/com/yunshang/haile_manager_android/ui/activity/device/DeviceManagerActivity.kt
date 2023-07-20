package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.DeviceManagerViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.SearchType
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.DeviceEntity
import com.yunshang.haile_manager_android.databinding.ActivityDeviceManagerBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceListBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.activity.personal.IncomeActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.NumberUtils
import com.yunshang.haile_manager_android.utils.UserPermissionUtils
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView


class DeviceManagerActivity :
    BaseBusinessActivity<ActivityDeviceManagerBinding, DeviceManagerViewModel>(
        DeviceManagerViewModel::class.java,
        BR.vm
    ) {

    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)?.let { json ->

                    GsonUtils.json2List(json, SearchSelectParam::class.java)?.let { selected ->
                        when (it.resultCode) {
                            IntentParams.SearchSelectTypeParam.ShopResultCode -> {
                                if (selected.isNotEmpty()) {
                                    mViewModel.selectDepartment.value = selected[0]
                                } else {
                                    mViewModel.selectDepartment.value = null
                                }
                            }
                            IntentParams.SearchSelectTypeParam.DeviceModelResultCode -> {
                                if (selected.isNotEmpty()) {
                                    mViewModel.selectDeviceModel.value = selected[0]
                                } else {
                                    mViewModel.selectDeviceModel.value = null
                                }
                            }
                        }
                    }
                }
            }
        }

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemDeviceListBinding, DeviceEntity>(
            R.layout.item_device_list,
            BR.item
        ) { mBinding, _, item ->

            val title =
                StringUtils.getString(R.string.total_earnings)
            val value =
                StringUtils.getString(R.string.unit_money) + NumberUtils.keepTwoDecimals(item.income)
            val start = title.length + 1
            val end = title.length + 1 + value.length
            // 格式化总收益样式
            mBinding?.tvItemDeviceTotalIncome?.text =
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
                        AbsoluteSizeSpan(DimensionUtils.sp2px(18f, this@DeviceManagerActivity)),
                        TypefaceSpan("money")
                    ), start, end
                )
            mBinding?.tvItemDeviceTotalIncome?.setOnClickListener {
                if (UserPermissionUtils.hasDeviceProfitPermission()) {
                    // 跳转到设备收益
                    startActivity(
                        Intent(
                            this@DeviceManagerActivity,
                            IncomeActivity::class.java
                        ).apply {
                            putExtra(IncomeActivity.ProfitType, 2)
                            putExtra(IncomeActivity.ProfitSearchId, item.id)
                            putExtra(IncomeActivity.DeviceName, item.name)
                        })
                }
            }

            // 进入详情
            mBinding?.root?.setOnClickListener {
                if (UserPermissionUtils.hasDeviceInfoPermission()) {
                    // 设备详情
                    startActivity(
                        Intent(
                            this@DeviceManagerActivity,
                            DeviceDetailActivity::class.java
                        ).apply {
                            putExtra(DeviceDetailActivity.GoodsId, item.id)
                        }
                    )
                }
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_device_manager

    override fun backBtn(): View = mBinding.barDeviceTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.searchKey.value = IntentParams.SearchParams.parseKeyWord(intent)
    }

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        if (mViewModel.searchKey.value.isNullOrEmpty()) {
            mBinding.barDeviceTitle.getRightBtn(true).run {
                setText(R.string.add_device)
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.mipmap.icon_add, 0, 0, 0
                )
                compoundDrawablePadding = DimensionUtils.dip2px(this@DeviceManagerActivity, 4f)
                setOnClickListener {
                    startActivity(
                        Intent(
                            this@DeviceManagerActivity,
                            DeviceCreateActivity::class.java
                        )
                    )
                }
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.viewDeviceManagerSearchBg.setOnClickListener {
            startActivity(Intent(this@DeviceManagerActivity, SearchActivity::class.java).apply {
                putExtra(SearchType.SearchType, SearchType.Device)
            })
        }

        // 所属门店
        mBinding.tvDeviceCategoryDepartment.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@DeviceManagerActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.SearchSelectTypeParam.pack(
                            IntentParams.SearchSelectTypeParam.SearchSelectTypeShop,
                            mustSelect = false
                        )
                    )
                }
            )
        }

        // 设备类型
        mBinding.tvDeviceCategoryCategory.setOnClickListener {
            mViewModel.categoryList.value?.let {
                showDeviceCategoryDialog(it)
            } ?: mViewModel.requestData(1)
        }

        // 设备模型
        mBinding.tvDeviceCategoryModel.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@DeviceManagerActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.SearchSelectTypeParam.pack(
                            IntentParams.SearchSelectTypeParam.SearchSelectTypeDeviceModel,
                            mViewModel.selectDeviceCategory.value?.id ?: -1,
                            mustSelect = false
                        )
                    )
                }
            )
        }

        // 网络状态
        mBinding.tvDeviceCategoryNetworkStatus.setOnClickListener {
            val deviceCategoryDialog =
                CommonBottomSheetDialog.Builder(
                    getString(R.string.network_status), arrayListOf(
                        SearchSelectParam(1, getString(R.string.online)),
                        SearchSelectParam(2, getString(R.string.offline)),
                        SearchSelectParam(4, getString(R.string.break_down)),
                    )
                ).apply {
                    mustSelect = false
                    onValueSureListener =
                        object : CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                            override fun onValue(data: SearchSelectParam?) {
                                mViewModel.selectNetworkStatus.value = data
                            }
                        }
                }
                    .build()
            deviceCategoryDialog.show(supportFragmentManager)
        }

        mBinding.tvDeviceCategoryDeviceStatus.setOnClickListener {
            CommonBottomSheetDialog.Builder(
                getString(R.string.device_status), arrayListOf(
                    SearchSelectParam(1, getString(R.string.enable)),
                    SearchSelectParam(2, getString(R.string.disEnable)),
                )
            ).apply {
                mustSelect = false
                selectData = mViewModel.selectDeviceStatus.value
                onValueSureListener =
                    object : CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                        override fun onValue(data: SearchSelectParam?) {
                            mViewModel.selectDeviceStatus.value = data
                        }
                    }
            }.build().show(supportFragmentManager)
        }

        // 设备状态
        mBinding.indicatorDeviceStatus.navigator = CommonNavigator(this).apply {

            adapter = object : CommonNavigatorAdapter() {
                override fun getCount(): Int = mViewModel.deviceStatus.size

                override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                    return SimplePagerTitleView(context).apply {
                        normalColor = Color.parseColor("#666666")
                        selectedColor = Color.WHITE
                        mViewModel.deviceStatus[index].run {
                            num.observe(this@DeviceManagerActivity) { n ->
                                text = title + if (0 < n) " $n" else " 0"
                            }
                            setOnClickListener {
                                mViewModel.curWorkStatus.value = value
                                onPageSelected(index)
                                notifyDataSetChanged()
                            }
                        }
                    }
                }

                override fun getIndicator(context: Context?): IPagerIndicator {
                    return WrapPagerIndicator(context).apply {
                        verticalPadding = DimensionUtils.dip2px(this@DeviceManagerActivity, 4f)
                        fillColor = ContextCompat.getColor(
                            this@DeviceManagerActivity,
                            R.color.colorPrimary
                        )
                        roundRadius =
                            DimensionUtils.dip2px(this@DeviceManagerActivity, 14f).toFloat()
                    }
                }
            }
        }

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
                ResourcesCompat.getDrawable(resources, R.drawable.divide_size8, null)?.let {
                    setDrawable(it)
                }
            })

        mBinding.rvDeviceManagerList.listStatusImgResId = R.mipmap.icon_list_device_empty
        mBinding.rvDeviceManagerList.listStatusTxtResId = R.string.empty_device
        mBinding.rvDeviceManagerList.adapter = mAdapter
        mBinding.rvDeviceManagerList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<DeviceEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out DeviceEntity>?) -> Unit
                ) {
                    if (UserPermissionUtils.hasDeviceListPermission()) {
                        mViewModel.requestDeviceList(page, pageSize, callBack)
                    }
                }
            }
    }

    override fun initEvent() {
        super.initEvent()
        mSharedViewModel.hasDeviceAddPermission.observe(this) {
            if (it)
                initRightBtn()
        }

        // 设备类型
        mViewModel.categoryList.observe(this) {
            showDeviceCategoryDialog(it)
        }

        // 选择店铺
        mViewModel.selectDepartment.observe(this) {
            mBinding.tvDeviceCategoryDepartment.text = it?.name ?: ""
            mBinding.rvDeviceManagerList.requestRefresh()
        }

        // 选择设备类型
        mViewModel.selectDeviceCategory.observe(this) {
            mBinding.tvDeviceCategoryCategory.text = it?.name ?: ""
            mBinding.rvDeviceManagerList.requestRefresh()
        }

        // 选择设备模型
        mViewModel.selectDeviceModel.observe(this) {
            mBinding.tvDeviceCategoryModel.text = it?.name ?: ""
            mBinding.rvDeviceManagerList.requestRefresh()
        }

        // 选择设备模型
        mViewModel.selectNetworkStatus.observe(this) {
            mBinding.tvDeviceCategoryNetworkStatus.text = it?.name ?: ""
            mBinding.rvDeviceManagerList.requestRefresh()
        }

        // 选择设备状态
        mViewModel.selectDeviceStatus.observe(this) {
            mBinding.tvDeviceCategoryDeviceStatus.text = it?.name ?: ""
            mBinding.rvDeviceManagerList.requestRefresh()
        }

        // 切换工作状态
        mViewModel.curWorkStatus.observe(this) {
            mBinding.rvDeviceManagerList.requestRefresh()
        }

        // 监听刷新
        LiveDataBus.with(BusEvents.DEVICE_LIST_STATUS)?.observe(this) {
            mViewModel.requestData(4)
            mBinding.rvDeviceManagerList.requestRefresh()
        }

        // 监听删除
        LiveDataBus.with(BusEvents.DEVICE_LIST_ITEM_DELETE_STATUS, Int::class.java)?.observe(this) {
            mViewModel.requestData(4)
            mAdapter.deleteItem { item -> item.id == it }
        }
    }

    /**
     * 显示设备类型弹窗
     */
    private fun showDeviceCategoryDialog(categoryEntities: List<CategoryEntity>) {
        val deviceCategoryDialog =
            CommonBottomSheetDialog.Builder(getString(R.string.device_category), categoryEntities)
                .apply {
                    mustSelect = false
                    onValueSureListener =
                        object : CommonBottomSheetDialog.OnValueSureListener<CategoryEntity> {
                            override fun onValue(data: CategoryEntity?) {
                                mViewModel.selectDeviceCategory.value = data
                                mViewModel.selectDeviceModel.value = null
                            }
                        }
                }
                .build()
        deviceCategoryDialog.show(supportFragmentManager)
    }

    override fun initData() {
        mViewModel.requestData(4)
    }
}