package com.shangyun.haile_manager_android.ui.activity.shop

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.AppointmentSettingViewModel
import com.shangyun.haile_manager_android.data.entities.AppointmentSettingEntity
import com.shangyun.haile_manager_android.databinding.ActivityShopAppointmentSettingBinding
import com.shangyun.haile_manager_android.databinding.ItemShopAppointmentSettingBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter

class ShopAppointmentSettingActivity :
    BaseBusinessActivity<ActivityShopAppointmentSettingBinding, AppointmentSettingViewModel>(AppointmentSettingViewModel::class.java,BR.vm) {

    companion object{
        const val ShopId = "shopId"
    }

    override fun layoutId(): Int = R.layout.activity_shop_appointment_setting

    override fun backBtn(): View = mBinding.barShopAppointmentTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()

        mViewModel.shopId = intent.getIntExtra(ShopId,-1)
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.rvShopAppointmentSettingList.layoutManager = LinearLayoutManager(this)
        mBinding.rvShopAppointmentSettingList.adapter =
            CommonRecyclerAdapter<ItemShopAppointmentSettingBinding, AppointmentSettingEntity>(
                R.layout.item_shop_appointment_setting,
                BR.item
            ) { mBinding, _, item ->
                mBinding?.switchShopAppointmentOpen?.setOnCheckedChangeListener { _, isChecked ->
                    item.appointSwitch = if (isChecked) 1 else 0
                }
            }
    }

    override fun initData() {
        mViewModel.requestData()
    }
}