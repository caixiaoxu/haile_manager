package com.yunshang.haile_manager_android.ui.activity.message

import android.graphics.Color
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.MessageSettingViewModel
import com.yunshang.haile_manager_android.data.entities.MessageSettingEntity
import com.yunshang.haile_manager_android.databinding.ActivityMessageSettingBinding
import com.yunshang.haile_manager_android.databinding.ItemMessageSettingBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter

class MessageSettingActivity :
    BaseBusinessActivity<ActivityMessageSettingBinding, MessageSettingViewModel>(
        MessageSettingViewModel::class.java
    ) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemMessageSettingBinding, MessageSettingEntity>(
            R.layout.item_message_setting, BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.switchMessageSettingOpen?.setOnSwitchClickListener {
                (it as SwitchCompat).isChecked.let { check ->
                    mViewModel.requestMessageSetting(
                        item.subtypeId,
                        if (check) 0 else 1
                    ) {
                        item.status
                        it.isChecked = !check
                    }
                }
                true
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_message_setting

    override fun backBtn(): View = mBinding.barMessageSettingTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()
        mViewModel.subTypeList.observe(this) {
            mAdapter.refreshList(it, true)
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.rvMessageSetting.layoutManager = LinearLayoutManager(this)
        mBinding.rvMessageSetting.adapter = mAdapter
    }

    override fun initData() {
        mViewModel.requestSubTypeList()
    }
}