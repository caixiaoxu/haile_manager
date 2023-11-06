package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.ShopAppointmentSettingViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.SettingItem
import com.yunshang.haile_manager_android.databinding.ActivityShopAppointmentSettingBinding
import com.yunshang.haile_manager_android.databinding.ItemShopAppointmentSettingBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog

class ShopAppointmentSettingActivity :
    BaseBusinessActivity<ActivityShopAppointmentSettingBinding, ShopAppointmentSettingViewModel>(
        ShopAppointmentSettingViewModel::class.java,
        BR.vm
    ) {

    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                when (it.resultCode) {
                    IntentParams.SearchSelectTypeParam.ShopResultCode -> {
                        intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)
                            ?.let { json ->
                                mViewModel.selectShops.value =
                                    GsonUtils.json2List(json, SearchSelectParam::class.java)
                            }
                    }
                }
            }
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

    override fun initEvent() {
        super.initEvent()
        mViewModel.selectShops.observe(this) {
            mViewModel.requestData()
        }

        mViewModel.appointmentSetting.observe(this) {
            mAdapter.refreshList(it.settingList, true)
        }
        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.llShopBatchAppointmentSettingShops.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@ShopAppointmentSettingActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.SearchSelectTypeParam.pack(
                            IntentParams.SearchSelectTypeParam.SearchSelectTypePaySettingsShop,
                            mustSelect = true,
                            moreSelect = true,
                            selectArr = mViewModel.selectShops.value?.map { item -> item.id }
                                ?.toIntArray() ?: intArrayOf()
                        )
                    )
                })
        }

        mBinding.rvShopAppointmentSettingList.layoutManager = LinearLayoutManager(this)
        mBinding.rvShopAppointmentSettingList.adapter = mAdapter

        mBinding.btnAppointmentSettingSave.setOnClickListener {
            if (2 == mViewModel.appointmentSetting.value?.reserveMethod && 1 != mViewModel.appointmentSetting.value?.autoRefund) {
                CommonDialog.Builder("不开启预约不使用，自动退款设置，则只有用户在预约验证时间到期前取消给退款，否则不退款").apply {
                    negativeTxt = StringUtils.getString(R.string.cancel)
                    setPositiveButton(StringUtils.getString(R.string.sure)) {
                        mViewModel.save(this@ShopAppointmentSettingActivity)
                    }
                }.build().show(supportFragmentManager)
            } else {
                mViewModel.save(this@ShopAppointmentSettingActivity)
            }
        }
    }

    override fun initData() {
        mViewModel.requestData()
    }
}