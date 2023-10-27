package com.yunshang.haile_manager_android.ui.fragment

import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.utils.ScreenUtils
import com.lsy.framelib.utils.StatusBarUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceMonitoringViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.databinding.FragmentDeviceMonitoringBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceMonitoringCategoryBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceMonitoringDetailCountBinding
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.view.CheckBoldRadioButton

/**
 * Title :
 * Author: Lsy
 * Date: 2023/10/20 11:25
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceMonitoringFragment :
    BaseBusinessFragment<FragmentDeviceMonitoringBinding, DeviceMonitoringViewModel>(
        DeviceMonitoringViewModel::class.java,
        BR.vm
    ) {
    private val countW = ScreenUtils.screenWidth / 4

    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)?.let { json ->
                    GsonUtils.json2List(json, SearchSelectParam::class.java)?.let { selected ->
                        when (it.resultCode) {
                            IntentParams.SearchSelectTypeParam.ShopResultCode -> {
                                mViewModel.selectDepartments.value = selected
                            }
                        }
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.fragment_device_monitoring

    override fun initEvent() {
        super.initEvent()

        val inflater = LayoutInflater.from(requireContext())
        mViewModel.categoryList.observe(this) {
            mBinding.rgDeviceMonitoringCategory.removeAllViews()
            it?.let { categoryList ->
                categoryList.forEachIndexed { index, deviceCategory ->
                    mBinding.rgDeviceMonitoringCategory.addView(
                        DataBindingUtil.inflate<ItemDeviceMonitoringCategoryBinding>(
                            inflater,
                            R.layout.item_device_monitoring_category,
                            null,
                            false
                        ).also { childBinding ->
                            childBinding.name = deviceCategory.categoryName
                            (childBinding.root as CheckBoldRadioButton).setOnCheckedChangeListener { _, b ->
                                if (b) {
                                    mViewModel.selectCategory.value = deviceCategory
                                }
                            }
                            childBinding.root.id = index + 1
                        }.root
                    )
                }
                mBinding.rgDeviceMonitoringCategory.check(mBinding.rgDeviceMonitoringCategory[0].id)
            }
        }

        mViewModel.deviceStateCounts.observe(this) { state ->
            state?.let {
                mBinding.ringDeviceMonitoringChart.data =
                    listOf(
                        state.workPercent / 100,
                        state.faultPercent / 100,
                        state.freePercent / 100
                    )
            }
        }

        mViewModel.deviceStateCountPercents.observe(this) {
            mBinding.glDeviceMonitoringNumPercent.removeAllViews()
            if (!it.isNullOrEmpty()) {
                it.forEachIndexed { index, percent ->
                    mBinding.glDeviceMonitoringNumPercent.addView(
                        DataBindingUtil.inflate<ItemDeviceMonitoringDetailCountBinding>(
                            inflater,
                            R.layout.item_device_monitoring_detail_count,
                            null,
                            false
                        ).also { childBinding ->
                            childBinding.itemW = countW
                            childBinding.name = percent.categoryName
                            childBinding.num = percent.count
                            childBinding.percent = percent.percent
                            if ((index % 4) < 3) {
                                childBinding.root.setBackgroundResource(R.drawable.shape_right_stroke_dividing_mtb16)
                            }
                        }.root
                    )
                }
            }
        }

        mViewModel.selectDepartments.observe(this) {
            mBinding.tvDeviceMonitoringShop.text =
                when (val count: Int? = it?.size) {
                    null, 0 -> ""
                    1 -> it.firstOrNull()?.name ?: ""
                    else -> "${count}家门店"
                }
            mViewModel.requestShopCategory()
        }

        mViewModel.selectCategory.observe(this) {
            mViewModel.requestGoodsCountPercents(0)
        }
    }

    override fun initView() {
        mBinding.countW = countW
        mBinding.root.setPadding(0, StatusBarUtils.getStatusBarHeight(), 0, 0)

        mBinding.tvDeviceMonitoringShop.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    requireContext(),
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.SearchSelectTypeParam.pack(
                            IntentParams.SearchSelectTypeParam.SearchSelectTypeShop,
                            mustSelect = false,
                            moreSelect = true,
                            selectArr = mViewModel.selectDepartments.value?.map { item -> item.id }
                                ?.toIntArray() ?: intArrayOf()
                        )
                    )
                }
            )
        }

        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.requestGoodsCountPercents(0)
        }
        mBinding.refreshLayout.setEnableLoadMore(false)
    }

    override fun initData() {
        mViewModel.requestShopCategory()
    }
}