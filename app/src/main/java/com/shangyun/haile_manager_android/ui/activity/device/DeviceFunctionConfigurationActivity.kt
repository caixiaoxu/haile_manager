package com.shangyun.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.DeviceFunctionConfigurationViewModel
import com.shangyun.haile_manager_android.data.arguments.DeviceCategory
import com.shangyun.haile_manager_android.data.entities.ExtAttrBean
import com.shangyun.haile_manager_android.data.entities.SkuEntity
import com.shangyun.haile_manager_android.data.entities.SkuFuncConfigurationParam
import com.shangyun.haile_manager_android.databinding.ActivityDeviceFunctionConfigurationBinding
import com.shangyun.haile_manager_android.databinding.ItemDeviceFuncConfigurationBinding
import com.shangyun.haile_manager_android.databinding.ItemDeviceFuncConfigurationDryerBinding
import com.shangyun.haile_manager_android.databinding.ItemDeviceFuncConfigurationDryerTimeBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.shangyun.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog

class DeviceFunctionConfigurationActivity :
    BaseBusinessActivity<ActivityDeviceFunctionConfigurationBinding, DeviceFunctionConfigurationViewModel>() {
    companion object {
        const val SpuId = "spuId"
        const val OldFuncConfiguration = "oldFuncConfiguration"
        const val ResultCode = 0x90003
        const val ResultData = "ResultData"
    }

    private val mDeviceFunctionConfigurationViewModel by lazy {
        getActivityViewModelProvider(this)[DeviceFunctionConfigurationViewModel::class.java]
    }


    private val mAdapter by lazy {
        if (DeviceCategory.isDryer(mDeviceFunctionConfigurationViewModel.categoryCode)) {
            val itemDryerHeight =
                DimensionUtils.dip2px(this@DeviceFunctionConfigurationActivity, 54f)

            CommonRecyclerAdapter<ItemDeviceFuncConfigurationDryerBinding, SkuEntity>(
                R.layout.item_device_func_configuration_dryer, BR.item
            ) { mBinding, pos, item ->
                mBinding?.tvDeviceFunConfigurationIndex?.text =
                    StringUtils.getString(R.string.device_func_configuration_title, pos + 1)

                mBinding?.llDeviceFunConfigurationDryerTime?.removeAllViews()
                if (!item.extAttrValue.isNullOrEmpty()) {
                    item.extAttrValue!!.filter { it.isCheck }.forEach { ext ->
                        val dryerTimeView =
                            LayoutInflater.from(this@DeviceFunctionConfigurationActivity).inflate(
                                R.layout.item_device_func_configuration_dryer_time,
                                null,
                                false
                            )
                        val dryerTimeBinding =
                            DataBindingUtil.bind<ItemDeviceFuncConfigurationDryerTimeBinding>(
                                dryerTimeView
                            )
                        dryerTimeBinding?.item = ext
                        dryerTimeBinding?.etDryerPulse?.visibility =
                            if (DeviceCategory.isPulseDevice(mDeviceFunctionConfigurationViewModel.communicationType))
                                View.VISIBLE else View.GONE
                        dryerTimeBinding?.tvDryerPulseHint?.visibility =
                            if (DeviceCategory.isPulseDevice(mDeviceFunctionConfigurationViewModel.communicationType))
                                View.VISIBLE else View.GONE
                        mBinding?.llDeviceFunConfigurationDryerTime?.addView(
                            dryerTimeView.rootView,
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, itemDryerHeight
                            )
                        )
                    }
                }

                mBinding?.mtivDeviceFunConfigurationDryerTime?.onSelectedEvent = {
                    if (item.extAttrValue.isNullOrEmpty()) {
                        item.extAttrValue =
                            GsonUtils.json2List(item.extAttr, ExtAttrBean::class.java)
                    }

                    item.extAttrValue?.let { exts -> showDryerTimeDialog(exts, pos) }
                }
            }
        } else {
            CommonRecyclerAdapter<ItemDeviceFuncConfigurationBinding, SkuEntity>(
                R.layout.item_device_func_configuration, BR.item
            ) { mBinding, pos, _ ->
                mBinding?.tvDeviceFunConfigurationIndex?.text =
                    String.format(
                        StringUtils.getString(R.string.device_func_configuration_title),
                        pos + 1
                    )

                mBinding?.mtivDeviceFunConfigurationPulseCount?.visibility =
                    if (DeviceCategory.isPulseDevice(mDeviceFunctionConfigurationViewModel.communicationType))
                        View.VISIBLE else View.GONE
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_device_function_configuration

    override fun getVM(): DeviceFunctionConfigurationViewModel =
        mDeviceFunctionConfigurationViewModel

    override fun backBtn(): View = mBinding.barDeviceFuncConfigurationTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mDeviceFunctionConfigurationViewModel.spuId = intent.getIntExtra(SpuId, -1)
        mDeviceFunctionConfigurationViewModel.categoryCode =
            intent.getStringExtra(DeviceCategory.CategoryCode)
        mDeviceFunctionConfigurationViewModel.communicationType =
            intent.getIntExtra(DeviceCategory.CommunicationType, -1)
        mDeviceFunctionConfigurationViewModel.oldConfigurationList = GsonUtils.json2List(
            intent.getStringExtra(OldFuncConfiguration),
            SkuFuncConfigurationParam::class.java
        )
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.rvDeviceFunConfigurationList.layoutManager = LinearLayoutManager(this)
        ContextCompat.getDrawable(
            this@DeviceFunctionConfigurationActivity,
            R.drawable.divder_f7f7f7_size8
        )?.let {
            mBinding.rvDeviceFunConfigurationList.addItemDecoration(
                DividerItemDecoration(
                    this,
                    DividerItemDecoration.VERTICAL
                ).apply { setDrawable(it) })
        }
        mBinding.rvDeviceFunConfigurationList.adapter = mAdapter

        //刷新数据
        mDeviceFunctionConfigurationViewModel.configurationList.observe(this) { it ->
            mAdapter.refreshList(it, true)
        }
    }

    override fun initEvent() {
        super.initEvent()

        mDeviceFunctionConfigurationViewModel.resultData.observe(this) {
            setResult(ResultCode, Intent().apply {
                putExtra(ResultData, GsonUtils.any2Json(it))
            })
            finish()
        }
    }

    override fun initData() {
        mBinding.vm = mDeviceFunctionConfigurationViewModel

        mDeviceFunctionConfigurationViewModel.requestData()
    }

    /**
     * 显示烘干机时间选择弹窗
     * @param exts 时间列表
     * @param pos item position
     */
    private fun showDryerTimeDialog(exts: List<ExtAttrBean>, pos: Int) {
        val dryerTimeDialog = MultiSelectBottomSheetDialog.Builder(
            StringUtils.getString(R.string.dryer_time_title),
            exts
        ).apply {
            onValueSureListener = object :
                MultiSelectBottomSheetDialog.OnValueSureListener<ExtAttrBean> {
                override fun onValue(datas: List<ExtAttrBean>) {
                    mAdapter.notifyItemChanged(pos)
                }
            }
        }.build()
        dryerTimeDialog.show(supportFragmentManager)
    }
}