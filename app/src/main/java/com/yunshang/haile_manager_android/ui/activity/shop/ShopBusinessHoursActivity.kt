package com.yunshang.haile_manager_android.ui.activity.shop

import android.graphics.Color
import android.view.View
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.ShopBusinessHoursViewModel
import com.yunshang.haile_manager_android.data.arguments.*
import com.yunshang.haile_manager_android.databinding.ActivityShopBusinessHoursBinding
import com.yunshang.haile_manager_android.databinding.ItemShopBusinessHoursBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import java.util.*

class ShopBusinessHoursActivity :
    BaseBusinessActivity<ActivityShopBusinessHoursBinding, ShopBusinessHoursViewModel>(
        ShopBusinessHoursViewModel::class.java,
        BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_shop_business_hours

    override fun backBtn(): View = mBinding.barShopBusinessTimeTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.initHourList(IntentParams.ShopBusinessHoursParams.parseShopBusinessHours(intent))
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.businessHourList.observe(this) {
            it?.let {
                mBinding.llBusinessHourList.buildChild<ItemShopBusinessHoursBinding, BusinessHourEntity>(
                    it
                ) { _, childBinding, data ->
                    childBinding.item = data
                    //选择日期
                    childBinding.tvShopBusinessDates.setOnClickListener {
                        showActiveDay(data)
                    }
                    childBinding.tvShopBusinessHours.setOnClickListener {
                        selectHours(data)
                    }

                    //删除
                    childBinding.ibShopBusinessHoursDel.setOnClickListener {
                        val list = mViewModel.businessHourList.value
                        if (!list.isNullOrEmpty()) {
                            list.remove(data)
                            val tempList =
                                mutableListOf<BusinessHourEntity>()
                            tempList.addAll(list)
                            mViewModel.businessHourList.value = tempList
                        }
                    }
                }
            }
        }
    }


    /**
     * 显示活动日
     */
    private fun showActiveDay(entity: BusinessHourEntity) {
        val list = mutableListOf<ActiveDayParam>()
        list.addAll(ShopParam.businessDay)
        list.forEach { type ->
            type.isCheck = entity.weekDays.contains(type)
        }

        MultiSelectBottomSheetDialog.Builder(
            StringUtils.getString(R.string.active_day),
            list
        ).apply {
            onValueSureListener = object :
                MultiSelectBottomSheetDialog.OnValueSureListener<ActiveDayParam> {
                override fun onValue(
                    selectList: List<ActiveDayParam>,
                    allSelectData: List<ActiveDayParam>
                ) {
                    entity.weekDays = selectList
                }
            }
        }.build().show(supportFragmentManager)
    }

    private fun selectHours(data: BusinessHourEntity) {
        DateSelectorDialog.Builder().apply {
            selectModel = 1
            showModel = 4
            onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                    date2?.let {
                        data.workTime = String.format(
                            "%s-%s",
                            DateTimeUtils.formatDateTime(date1, "HH:mm"),
                            DateTimeUtils.formatDateTime(it, "HH:mm")
                        )
                    }
                }
            }
        }.build().show(supportFragmentManager)
    }


    override fun initView() {
        window.statusBarColor = Color.WHITE
    }

    override fun initData() {
    }
}