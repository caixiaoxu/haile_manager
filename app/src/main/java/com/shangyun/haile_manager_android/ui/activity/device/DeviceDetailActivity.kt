package com.shangyun.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.ScreenUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.event.BusEvents
import com.shangyun.haile_manager_android.business.vm.DeviceDetailModel
import com.shangyun.haile_manager_android.business.vm.DeviceMultiChangeViewModel
import com.shangyun.haile_manager_android.data.arguments.DeviceCategory
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.entities.SkuFuncConfigurationParam
import com.shangyun.haile_manager_android.databinding.ActivityDeviceDetailBinding
import com.shangyun.haile_manager_android.databinding.ItemDeviceDetailAppointmentBinding
import com.shangyun.haile_manager_android.databinding.ItemDeviceDetailFuncPriceBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.activity.common.SearchSelectRadioActivity

class DeviceDetailActivity :
    BaseBusinessActivity<ActivityDeviceDetailBinding, DeviceDetailModel>() {

    companion object {
        const val GoodsId = "goodsId"
    }

    private val mDeviceDetailModel by lazy {
        getActivityViewModelProvider(this)[DeviceDetailModel::class.java]
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
                                    mDeviceDetailModel.deviceDetail.value?.imei = it
                                    mDeviceDetailModel.imei.value = it
                                }
                                DeviceMultiChangeViewModel.typeChangePayCode -> {
                                    mDeviceDetailModel.deviceDetail.value?.code = it
                                    mDeviceDetailModel.code.value = it
                                }
                                DeviceMultiChangeViewModel.typeChangeName -> {
                                    mDeviceDetailModel.deviceDetail.value?.name = it
                                    mDeviceDetailModel.name.value = it
                                }
                            }
                        }
                    }
                }
            }
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
                    setTextColor(
                        ContextCompat.getColor(
                            this@DeviceDetailActivity,
                            R.color.colorPrimary
                        )
                    )
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

    override fun initIntent() {
        super.initIntent()
        mDeviceDetailModel.goodsId = intent.getIntExtra(GoodsId, -1)
    }

    override fun initEvent() {
        super.initEvent()

        val mTB = DimensionUtils.dip2px(this, 12f)
        mDeviceDetailModel.deviceDetail.observe(this) { detail ->
            mBinding.llDeviceDetailFuncPrice.removeAllViews()
            detail?.items?.forEachIndexed { index, item ->
                val itemBinding = LayoutInflater.from(this@DeviceDetailActivity)
                    .inflate(R.layout.item_device_detail_func_price, null, false).let { view ->
                        DataBindingUtil.bind<ItemDeviceDetailFuncPriceBinding>(view)
                    }
                itemBinding?.let {
                    itemBinding.item = item
                    itemBinding.isDryer = DeviceCategory.isDryer(detail.categoryCode)
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

            mBinding.llDeviceDetailAppointment.removeAllViews()
            detail?.items?.forEachIndexed { index, item ->
                val itemBinding = LayoutInflater.from(this@DeviceDetailActivity)
                    .inflate(R.layout.item_device_detail_appointment, null, false).let { view ->
                        DataBindingUtil.bind<ItemDeviceDetailAppointmentBinding>(view)
                    }
                itemBinding?.let {
                    itemBinding.item = item
                    mBinding.llDeviceDetailAppointment.addView(
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
        mDeviceDetailModel.deviceAdvancedValues.observe(this) {
            if (!it.isNullOrEmpty()) {
                initRightBtn()
            }
        }

        mDeviceDetailModel.jump.observe(this) {
            when (it) {
                0 -> finish()
                // 修改功能价格
                1 -> {
                    mDeviceDetailModel.deviceDetail.value?.let { detail ->
                        startActivity(
                            Intent(
                                this@DeviceDetailActivity,
                                DeviceFunctionConfigurationActivity::class.java
                            ).apply {
                                putExtra(
                                    DeviceFunctionConfigurationActivity.GoodId,
                                    mDeviceDetailModel.goodsId
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
                2 -> mDeviceDetailModel.deviceDetail.value?.let { detail ->
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
                            mDeviceDetailModel.goodsId
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
                            mDeviceDetailModel.goodsId
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
                            mDeviceDetailModel.goodsId
                        )
                        putExtra(
                            DeviceMultiChangeViewModel.Type,
                            DeviceMultiChangeViewModel.typeChangeName
                        )
                    }
                )
                // 生成付款码
                6 -> mDeviceDetailModel.deviceDetail.value?.let { detail ->
                    startActivity(
                        Intent(
                            this@DeviceDetailActivity,
                            DevicePayCodeActivity::class.java
                        ).apply {
                            putExtra(DevicePayCodeActivity.Code, detail.code)
                        })
                }
            }
        }

        // 监听刷新
        LiveDataBus.with(BusEvents.DEVICE_DETAILS_STATUS)?.observe(this) {
            mDeviceDetailModel.requestData()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 初始化功能操作区
        val itemW = ScreenUtils.screenWidth / mBinding.glDeviceDetailFunc.columnCount
        val pLR = DimensionUtils.dip2px(this, 8f)
        val pTB = DimensionUtils.dip2px(this, 16f)
        mDeviceDetailModel.getDeviceDetailFunOperate(
            mSharedViewModel.hasDeviceResetPermission,
            mSharedViewModel.hasDeviceStartPermission,
            mSharedViewModel.hasDeviceCleanPermission,
            mSharedViewModel.hasDeviceQrcodePermission,
            mSharedViewModel.hasDeviceUpdatePermission,
            mSharedViewModel.hasDeviceAppointmentPermission,
        ).forEachIndexed { index, config ->
            (LayoutInflater.from(this@DeviceDetailActivity)
                .inflate(R.layout.item_device_detail_func, null, false) as AppCompatTextView).also {
                it.text = config.title
                it.setCompoundDrawablesRelativeWithIntrinsicBounds(0, config.icon, 0, 0)
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
    }

    override fun initData() {
        mBinding.vm = mDeviceDetailModel
        mBinding.shared = mSharedViewModel

        mDeviceDetailModel.requestData()
    }
}