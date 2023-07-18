package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.ScreenUtils
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.DeviceDetailModel
import com.yunshang.haile_manager_android.business.vm.DeviceMultiChangeViewModel
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.Item
import com.yunshang.haile_manager_android.databinding.ActivityDeviceDetailBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceDetailFuncPriceBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog

class DeviceDetailActivity :
    BaseBusinessActivity<ActivityDeviceDetailBinding, DeviceDetailModel>(
        DeviceDetailModel::class.java,
        BR.vm
    ) {

    companion object {
        const val GoodsId = "goodsId"
    }

    // 跳转修改界面
    private val startNext =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                DeviceMultiChangeActivity.ResultCode -> {
                    result.data?.let { intent ->
                        intent.getStringExtra(DeviceMultiChangeActivity.ResultData)?.let {
                            when (intent.getIntExtra(DeviceMultiChangeViewModel.Type, -1)) {
                                DeviceMultiChangeViewModel.typeChangeModel -> {
                                    mViewModel.deviceDetail.value?.imei = it
                                    mViewModel.imei.value = it
                                }
                                DeviceMultiChangeViewModel.typeChangePayCode -> {
                                    mViewModel.deviceDetail.value?.code = it
                                    mViewModel.code.value = it
                                }
                                DeviceMultiChangeViewModel.typeChangeName -> {
                                    mViewModel.deviceDetail.value?.name = it
                                    mViewModel.name.value = it
                                }
                            }
                        }
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_device_detail

    override fun backBtn(): View = mBinding.barDeviceDetailTitle.getBackBtn()

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        mBinding.barDeviceDetailTitle.getRightBtn().run {
            setText(R.string.advanced_setup)
            setTextColor(
                ContextCompat.getColor(
                    this@DeviceDetailActivity,
                    R.color.colorPrimary
                )
            )
            setOnClickListener {
                mViewModel.deviceAdvancedValues.value?.let { values ->
                    // 高级设置
                    startActivity(
                        Intent(
                            this@DeviceDetailActivity,
                            DeviceAdvancedActivity::class.java
                        ).apply {
                            putExtra(
                                DeviceAdvancedActivity.GoodId,
                                mViewModel.goodsId
                            )
                            putExtra(
                                DeviceAdvancedActivity.Advanced,
                                GsonUtils.any2Json(values)
                            )
                        }
                    )
                }
            }
        }
    }

    override fun initIntent() {
        super.initIntent()
        mViewModel.goodsId = intent.getIntExtra(GoodsId, -1)
    }

    override fun initEvent() {
        super.initEvent()

        val mTB = DimensionUtils.dip2px(this, 12f)
        mViewModel.deviceDetail.observe(this) { detail ->
            if (!detail.shopAppointmentEnabled) {
                mBinding.glDeviceDetailFunc.children.find {
                    it.tag == R.mipmap.icon_device_device_appointment_setting
                }?.let {
                    mBinding.glDeviceDetailFunc.removeView(it)
                }
            }

            // 吹风机不显示桶自洁
            if (DeviceCategory.isHair(detail.categoryCode)) {
                mViewModel.deviceDetailFunOperate.find { item -> item.icon == R.mipmap.icon_device_self_clean }
                    ?.let { item ->
                        item.show.value = false
                    }
            }

            mBinding.llDeviceDetailFuncPrice.removeAllViews()
            detail?.items?.forEachIndexed { index, item ->
                val itemBinding = LayoutInflater.from(this@DeviceDetailActivity)
                    .inflate(R.layout.item_device_detail_func_price, null, false).let { view ->
                        DataBindingUtil.bind<ItemDeviceDetailFuncPriceBinding>(view)
                    }
                itemBinding?.let {
                    itemBinding.item = item
                    itemBinding.isDryer = DeviceCategory.isDryerOrHair(detail.categoryCode)
                    itemBinding.deviceCommunicationType =
                        detail.communicationType
                    itemBinding.tvFunPriceDesc.visibility =
                        if (DeviceCategory.isHair(detail.categoryCode)) View.GONE else View.VISIBLE
                    mBinding.llDeviceDetailFuncPrice.addView(
                        itemBinding.root, LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                        ).apply {
                            setMargins(0, if (0 == index) mTB else 0, 0, mTB)
                        }
                    )
                }
            }
        }

        // 是否有高级参数设置
        mViewModel.deviceAdvancedValues.observe(this) {
            if (!it.isNullOrEmpty()) {
                initRightBtn()
            }
        }

        mViewModel.jump.observe(this) {
            when (it) {
                0 -> finish()
                // 修改功能价格
                1 -> {
                    mViewModel.deviceDetail.value?.let { detail ->
                        startActivity(
                            Intent(
                                this@DeviceDetailActivity,
                                DeviceFunctionConfigurationActivity::class.java
                            ).apply {
                                putExtra(
                                    DeviceFunctionConfigurationActivity.GoodId,
                                    mViewModel.goodsId
                                )
                                putExtra(
                                    DeviceFunctionConfigurationActivity.SpuId,
                                    detail.spuId
                                )
                                putExtra(
                                    DeviceCategory.CategoryCode,
                                    detail.categoryCode
                                )
                                putExtra(
                                    DeviceCategory.CommunicationType,
                                    detail.communicationType
                                )
                                if (detail.items.isNotEmpty()) {
                                    putExtra(
                                        DeviceFunctionConfigurationActivity.OldFuncConfiguration,
                                        GsonUtils.any2Json(detail.items.map { item -> item.changeConfigurationParam() })
                                    )
                                }
                            }
                        )
                    }
                }
                // 启动
                2 -> mViewModel.deviceDetail.value?.let { detail ->
                    startActivity(
                        Intent(
                            this@DeviceDetailActivity,
                            DeviceStartActivity::class.java
                        ).apply {
                            putExtra(DeviceStartActivity.Imei, detail.imei)
                            putExtra(DeviceCategory.CategoryCode, detail.categoryCode)
                            putExtra(DeviceStartActivity.Items, GsonUtils.any2Json(detail.items))
                        })
                }
                // 更换模块
                3 -> startNext.launch(
                    Intent(
                        this@DeviceDetailActivity,
                        DeviceMultiChangeActivity::class.java
                    ).apply {
                        putExtra(
                            DeviceMultiChangeActivity.GoodId,
                            mViewModel.goodsId
                        )
                        putExtra(
                            DeviceMultiChangeViewModel.Type,
                            DeviceMultiChangeViewModel.typeChangeModel
                        )
                    }
                )
                // 更换付款码
                4 -> startNext.launch(
                    Intent(
                        this@DeviceDetailActivity,
                        DeviceMultiChangeActivity::class.java
                    ).apply {
                        putExtra(
                            DeviceMultiChangeActivity.GoodId,
                            mViewModel.goodsId
                        )
                        putExtra(
                            DeviceMultiChangeViewModel.Type,
                            DeviceMultiChangeViewModel.typeChangePayCode
                        )
                    }
                )
                // 更换名称
                5 -> startNext.launch(
                    Intent(
                        this@DeviceDetailActivity,
                        DeviceMultiChangeActivity::class.java
                    ).apply {
                        putExtra(
                            DeviceMultiChangeActivity.GoodId,
                            mViewModel.goodsId
                        )
                        putExtra(
                            DeviceMultiChangeViewModel.Type,
                            DeviceMultiChangeViewModel.typeChangeName
                        )
                    }
                )
                // 生成付款码
                6 -> mViewModel.deviceDetail.value?.let { detail ->
                    startActivity(
                        Intent(
                            this@DeviceDetailActivity,
                            DevicePayCodeActivity::class.java
                        ).apply {
                            putExtra(DevicePayCodeActivity.Code, detail.scanUrl)
                        })
                }
                // 显示预约
                7 -> mViewModel.deviceDetail.value?.let { detail ->
                    if (20 == detail.communicationType) {
                        SToast.showToast(this@DeviceDetailActivity, "当前功能不支撑脉冲设备");
                        return@let
                    }
                    if (null == detail.appointmentEnabled) {
                        SToast.showToast(this@DeviceDetailActivity, "当前功能不可用");
                        return@let
                    }
                    CommonDialog.Builder(
                        "是否${if (detail.appointmentEnabled!!) "关闭" else "开启"}该设备预约功能"
                    ).apply {
                        negativeTxt = "否"
                        setPositiveButton("是") {
                            mViewModel.openOrCloseAppointment(!detail.appointmentEnabled!!) {
                                SToast.showToast(
                                    this@DeviceDetailActivity,
                                    if (!detail.appointmentEnabled!!) R.string.open_success else R.string.close_success
                                )
                                detail.appointmentEnabled = !detail.appointmentEnabled!!
                            }
                        }
                    }.build().show(supportFragmentManager)
                }
                // 复位
                8 -> mViewModel.deviceDetail.value?.let { detail ->
                    if (DeviceCategory.isHair(detail.categoryCode)) {
                        CommonBottomSheetDialog.Builder(
                            StringUtils.getString(R.string.restart_dialog_title),
                            detail.items.filter { item -> 1 == item.soldState }
                        ).apply {
                            onValueSureListener =
                                object : CommonBottomSheetDialog.OnValueSureListener<Item> {
                                    override fun onValue(data: Item?) {
                                        mViewModel.deviceOperate(0, data?.id)
                                    }
                                }
                        }.build().show(supportFragmentManager)
                    } else {
                        mViewModel.deviceOperate(0)
                    }
                }
            }
        }

        // 监听刷新
        LiveDataBus.with(BusEvents.DEVICE_DETAILS_STATUS)?.observe(this) {
            mViewModel.requestData(1)
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 初始化功能操作区
        val itemW = ScreenUtils.screenWidth / mBinding.glDeviceDetailFunc.columnCount
        val pLR = DimensionUtils.dip2px(this, 8f)
        val pTB = DimensionUtils.dip2px(this, 16f)
        mViewModel.deviceDetailFunOperate.forEachIndexed { index, config ->
            (LayoutInflater.from(this@DeviceDetailActivity)
                .inflate(R.layout.item_device_detail_func, null, false) as AppCompatTextView).also {
                it.text = config.title
                it.tag = config.icon
                it.setCompoundDrawablesWithIntrinsicBounds(0, config.icon, 0, 0)
                it.setPadding(
                    pLR,
                    if (0 == index % mBinding.glDeviceDetailFunc.columnCount) pTB else 0,
                    pLR,
                    pTB
                )
                config.show.observe(this) { show ->
                    if (!show) {
                        mBinding.glDeviceDetailFunc.removeView(it)
                    }
                }
                it.setOnClickListener(config.onClick)
                mBinding.glDeviceDetailFunc.addView(
                    it, ViewGroup.LayoutParams(itemW, ViewGroup.LayoutParams.WRAP_CONTENT)
                )
            }
        }

        mBinding.btnDeviceDetailDelete.setOnClickListener {
            CommonDialog.Builder(StringUtils.getString(R.string.device_delete_hint)).apply {
                negativeTxt = StringUtils.getString(R.string.cancel)
                setPositiveButton(StringUtils.getString(R.string.delete)) {
                    mViewModel.deviceDelete()
                }
            }.build()
                .show(supportFragmentManager)
        }
    }

    override fun initData() {
        mBinding.shared = mSharedViewModel

        mViewModel.requestData()
    }
}