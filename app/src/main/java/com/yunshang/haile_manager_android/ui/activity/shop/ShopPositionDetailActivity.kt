package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.ViewUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.ShopPositionDetailViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityShopPositionDetailBinding
import com.yunshang.haile_manager_android.databinding.ItemOrderDetailInfoBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog

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
        mViewModel.positionDetail.observe(this) {
            it.serviceTelephone?.let { phoneStr ->
                mBinding.llShopPositionDetailPhone.buildChild<ItemOrderDetailInfoBinding, String>(
                    phoneStr.split(","),
                    start = 1
                ) { index, childBinding, data ->
                    childBinding.canShow = data.isNotEmpty()
                    childBinding.content = data
                    childBinding.itemTitle =
                        if (0 == index) com.lsy.framelib.utils.StringUtils.getString(R.string.contact_phone) + "：" else ""
                    childBinding.showCopy = true
                }
            }
        }

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
                if (1 == detail.state) {
                    CommonDialog.Builder("停用后，不可添加设备，用户不可在本点位下单。").apply {
                        negativeTxt = com.lsy.framelib.utils.StringUtils.getString(R.string.cancel)
                        setPositiveButton(com.lsy.framelib.utils.StringUtils.getString(R.string.sure)) {
                            mViewModel.changePositionState(2)
                        }
                    }.build().show(supportFragmentManager)
                } else {
                    mViewModel.changePositionState(1)
                }
            }
            true
        }

        // 编辑
        mBinding.btnPositionDetailEdit.setOnClickListener {
            mViewModel.positionDetail.value?.let { detail ->
                if (!ViewUtils.isFastDoubleClick()) {
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
        }

        // 删除
        mBinding.btnPositionDetailDelete.setOnClickListener {
            if (!ViewUtils.isFastDoubleClick()) {
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
    }

    override fun initData() {
        mViewModel.requestPositionDetail()
    }
}