package com.shangyun.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.lsy.framelib.utils.DimensionUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.DeviceDetailModel
import com.shangyun.haile_manager_android.databinding.ActivityDeviceDetailBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.activity.shop.ShopCreateAndUpdateActivity

class DeviceDetailActivity :
    BaseBusinessActivity<ActivityDeviceDetailBinding, DeviceDetailModel>() {

    companion object {
        const val GoodsId = "goodsId"
    }

    private val mDeviceDetailModel by lazy {
        getActivityViewModelProvider(this)[DeviceDetailModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_device_detail

    override fun getVM(): DeviceDetailModel = mDeviceDetailModel

    override fun backBtn(): View = mBinding.barDeviceDetailTitle.getBackBtn()

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        mBinding.barDeviceDetailTitle.getRightArea()
            .addView(
                TextView(this).apply {
                    setText(R.string.advanced_setup)
                    setTextColor(ContextCompat.getColor(this@DeviceDetailActivity,R.color.colorPrimary))
                    textSize = 14f
                    setOnClickListener {
                        // 高级设置
                    }
                },
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                ).apply {
                    setMargins(0, 0, DimensionUtils.dip2px(this@DeviceDetailActivity, 16f), 0)
                }
            )
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        initRightBtn()
    }

    override fun initData() {
    }
}