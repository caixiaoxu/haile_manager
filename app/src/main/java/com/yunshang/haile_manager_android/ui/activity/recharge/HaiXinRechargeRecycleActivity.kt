package com.yunshang.haile_manager_android.ui.activity.recharge

import android.graphics.Color
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.HaiXinRechargeRecycleViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityHaixinRechargeRecycleBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class HaiXinRechargeRecycleActivity :
    BaseBusinessActivity<ActivityHaixinRechargeRecycleBinding, HaiXinRechargeRecycleViewModel>(
        HaiXinRechargeRecycleViewModel::class.java, BR.vm
    ) {
    override fun layoutId(): Int = R.layout.activity_haixin_recharge_recycle

    override fun backBtn(): View = mBinding.barRechargeRecycleTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.userId = IntentParams.RechargeRecycleParams.parseUserId(intent)
        mViewModel.shopId = IntentParams.RechargeRecycleParams.parseShopId(intent)
        mViewModel.account.value = IntentParams.RechargeRecycleParams.parsePhone(intent)
        mViewModel.shop.value = IntentParams.RechargeRecycleParams.parseShopName(intent)

        mViewModel.reach.value = IntentParams.RechargeRecycleParams.parseReach(intent)
        mViewModel.reward.value = IntentParams.RechargeRecycleParams.parseReward(intent)
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
    }

    override fun initData() {
        mViewModel.requestData()
    }
}