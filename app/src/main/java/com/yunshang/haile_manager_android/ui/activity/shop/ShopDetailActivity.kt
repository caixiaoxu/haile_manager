package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.ShopDetailViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityShopDetailBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog

class ShopDetailActivity : BaseBusinessActivity<ActivityShopDetailBinding, ShopDetailViewModel>(
    ShopDetailViewModel::class.java,
    BR.vm
) {

    override fun layoutId(): Int = R.layout.activity_shop_detail

    override fun backBtn(): View = mBinding.barShopDetailTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.shopId = IntentParams.ShopParams.parseShopId(intent)
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 运营设置
        mBinding.btnShopDetailOperationSetting.setOnClickListener {
            startActivity(
                Intent(
                    this@ShopDetailActivity,
                    ShopOperationSettingActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.ShopParams.pack(mViewModel.shopId)
                    )
                }
            )
        }

        // 编辑
        mBinding.btnShopDetailEdit.setOnClickListener {
            startActivity(
                Intent(
                    this@ShopDetailActivity,
                    ShopCreateAndUpdateActivity::class.java
                ).apply {
                    putExtra(
                        ShopCreateAndUpdateActivity.ShopDetail,
                        GsonUtils.any2Json(mViewModel.shopDetail.value!!)
                    )
                })
        }

        // 删除
        mBinding.btnShopDetailDelete.setOnClickListener {
            CommonDialog.Builder(StringUtils.getString(R.string.shop_delete_hint)).apply {
                negativeTxt = StringUtils.getString(R.string.cancel)
                setPositiveButton(StringUtils.getString(R.string.delete)) {
                    mViewModel.requestShopDelete()
                }
            }.build()
                .show(supportFragmentManager)
        }
    }

    override fun initEvent() {
        super.initEvent()

        mSharedViewModel.hasShopUpdatePermission.observe(this) {
            mBinding.btnShopDetailEdit.visibility = if (it) View.VISIBLE else View.GONE
        }

        mSharedViewModel.hasShopDeletePermission.observe(this) {
            mBinding.btnShopDetailDelete.visibility =
                if (it) View.VISIBLE else View.GONE
        }

        // 修改成功后
        LiveDataBus.with(BusEvents.SHOP_DETAILS_STATUS)?.observe(this) {
            mViewModel.requestShopDetail()
        }

        mViewModel.jump.observe(this) {
            finish()
        }

    }

    override fun initData() {
        mViewModel.requestShopDetail()
    }
}