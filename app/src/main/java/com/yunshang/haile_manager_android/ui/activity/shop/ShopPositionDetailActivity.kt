package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.lsy.framelib.async.LiveDataBus
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.ShopPositionDetailViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityShopPositionDetailBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog
import com.yunshang.haile_manager_android.utils.StringUtils

class ShopPositionDetailActivity :
    BaseBusinessActivity<ActivityShopPositionDetailBinding, ShopPositionDetailViewModel>(
        ShopPositionDetailViewModel::class.java, BR.vm
    ) {
    override fun layoutId(): Int = R.layout.activity_shop_position_detail

    override fun backBtn(): View = mBinding.barPositionDetailTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.positionId = IntentParams.CommonParams.parseId(intent)
    }

    override fun initEvent() {
        super.initEvent()

        // 修改成功后
        LiveDataBus.with(BusEvents.PT_DETAILS_STATUS)?.observe(this) {
            mViewModel.requestPositionDetail()
        }

        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 切换状态
        mBinding.switchPositionDetailOpen.setOnSwitchClickListener {
            mViewModel.positionDetail.value?.let { detail ->
                mViewModel.changePositionState(
                    if (1 == detail.state) 2 else 1
                )
            }
            true
        }

        // 复制客服电话
        mBinding.tvPositionDetailServicePhoneCopy.setOnClickListener {
            StringUtils.copyToShear(mViewModel.positionDetail.value?.serviceTelephone ?: "")
        }

        // 编辑
        mBinding.btnPositionDetailEdit.setOnClickListener {
            mViewModel.positionDetail.value?.let { detail ->
                startActivity(
                    Intent(
                        this@ShopPositionDetailActivity,
                        ShopPositionCreateActivity::class.java
                    ).apply {
                        putExtras(
                            IntentParams.ShopPositionCreateParams.pack(detail)
                        )
                    })
            }
        }

        // 删除
        mBinding.btnPositionDetailDelete.setOnClickListener {
            CommonDialog.Builder(com.lsy.framelib.utils.StringUtils.getString(R.string.pt_delete_hint))
                .apply {
                    negativeTxt = com.lsy.framelib.utils.StringUtils.getString(R.string.cancel)
                    setPositiveButton(com.lsy.framelib.utils.StringUtils.getString(R.string.delete)) {
                        mViewModel.deletePosition()
                    }
                }.build()
                .show(supportFragmentManager)
        }
    }

    override fun initData() {
        mViewModel.requestPositionDetail()
    }
}