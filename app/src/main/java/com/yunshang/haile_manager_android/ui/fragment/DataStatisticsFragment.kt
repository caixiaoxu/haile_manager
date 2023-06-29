package com.yunshang.haile_manager_android.ui.fragment

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.library.baseAdapters.BR
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DataStatisticsViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.databinding.FragmentDataStatisticsBinding
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import java.util.*

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/20 15:37
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DataStatisticsFragment :
    BaseBusinessFragment<FragmentDataStatisticsBinding, DataStatisticsViewModel>(
        DataStatisticsViewModel::class.java,
        BR.vm
    ) {

    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)?.let { json ->

                    GsonUtils.json2List(json, SearchSelectParam::class.java)?.let { selected ->
                        if (selected.isNotEmpty()) {
                            when (it.resultCode) {
                                IntentParams.SearchSelectTypeParam.ShopResultCode -> {
                                    mViewModel.selectDepartment.value = selected[0]
                                }
                            }
                        } else mViewModel.selectDepartment.value = null
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.fragment_data_statistics

    override fun initEvent() {
        super.initEvent()

        mViewModel.dateVal.observe(this) {
            mBinding.includeDataStatisticsFilter.tvDataStatisticsTime.text = it
        }
        mViewModel.selectDepartment.observe(this) {
            it?.let {
                mBinding.includeDataStatisticsFilter.tvDataStatisticsShop.text = it.name
            }
        }

        // 设备类型
        mViewModel.categoryList.observe(this) {
            showDeviceCategoryDialog(it)
        }

        // 选择设备类型
        mViewModel.selectDeviceCategory.observe(this) {
            it?.let {
                mBinding.includeDataStatisticsFilter.tvDataStatisticsDevice.text = it.name
            }
        }
    }

    override fun initView() {
        // 日、周、月、年
        mBinding.includeDataStatisticsDate.rgDataStatisticsCategoryDate.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_data_statistics_category_day -> mViewModel.dateType.value = 1
                R.id.rb_data_statistics_category_week -> mViewModel.dateType.value = 2
                R.id.rb_data_statistics_category_month -> mViewModel.dateType.value = 3
                R.id.rb_data_statistics_category_year -> mViewModel.dateType.value = 4
            }
        }

        // 日期选择
        mBinding.includeDataStatisticsFilter.tvDataStatisticsTime.setOnClickListener {
            DateSelectorDialog.Builder().apply {
                selectModel = if (1 == mViewModel.dateType.value) 1 else 0
                showModel = when (mViewModel.dateType.value) {
                    2 -> 7
                    3 -> 1
                    4 -> 6
                    else -> 0
                }
                maxDate = Calendar.getInstance().apply { time = DateTimeUtils.beforeDay(Date(), 1) }
                limitSpace = 31
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        if (1 == mViewModel.dateType.value || 2 == mViewModel.dateType.value) {
                            mViewModel.startTime.value = date1
                            date2?.let {
                                mViewModel.endTime.value = date2
                            }
                        } else {
                            mViewModel.singleTime.value = date1
                        }
                    }
                }
            }.build().show(
                childFragmentManager,
                when (mViewModel.dateType.value) {
                    1 -> mViewModel.startTime.value
                    2 -> mViewModel.endTime.value
                    else -> mViewModel.singleTime.value
                },
                if (1 == mViewModel.dateType.value) mViewModel.endTime.value else null
            )
        }

        // 店铺
        mBinding.includeDataStatisticsFilter.tvDataStatisticsShop.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    requireContext(),
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
        mBinding.includeDataStatisticsFilter.tvDataStatisticsDevice.setOnClickListener {
            mViewModel.categoryList.value?.let {
                showDeviceCategoryDialog(it)
            } ?: mViewModel.requestDeviceCategory()
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
                            }
                        }
                }
                .build()
        deviceCategoryDialog.show(childFragmentManager)
    }

    override fun initData() {
    }
}