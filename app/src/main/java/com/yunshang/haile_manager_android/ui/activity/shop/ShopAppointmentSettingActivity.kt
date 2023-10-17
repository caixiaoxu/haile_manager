package com.yunshang.haile_manager_android.ui.activity.shop

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.AppointmentSettingViewModel
import com.yunshang.haile_manager_android.data.entities.AppointmentSettingEntity
import com.yunshang.haile_manager_android.data.entities.SettingItem
import com.yunshang.haile_manager_android.databinding.ActivityShopAppointmentSettingBinding
import com.yunshang.haile_manager_android.databinding.ItemShopAppointmentSettingBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter

class ShopAppointmentSettingActivity :
    BaseBusinessActivity<ActivityShopAppointmentSettingBinding, AppointmentSettingViewModel>(
        AppointmentSettingViewModel::class.java,
        BR.vm
    ) {

    companion object {
        const val ShopId = "shopId"
    }

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemShopAppointmentSettingBinding, SettingItem>(
            R.layout.item_shop_appointment_setting,
            BR.item
        ) { _, _, _ ->
        }
    }

    override fun layoutId(): Int = R.layout.activity_shop_appointment_setting

    override fun backBtn(): View = mBinding.barShopAppointmentTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.shopId = intent.getIntExtra(ShopId, -1)
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.appointmentSetting.observe(this) {
            mAdapter.refreshList(it.settingList, true)
        }
        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.rvShopAppointmentSettingList.layoutManager = LinearLayoutManager(this)
        mBinding.rvShopAppointmentSettingList.adapter = mAdapter

    }

    override fun initData() {
        mViewModel.requestData()
    }
}