package com.yunshang.haile_manager_android.ui.activity.device

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceFunConfigurationV2ViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.ExtAttrDtoItem
import com.yunshang.haile_manager_android.data.entities.SkuFunConfigurationV2Param
import com.yunshang.haile_manager_android.databinding.ActivityDeviceFunConfigurationV2Binding
import com.yunshang.haile_manager_android.databinding.ItemDeviceFunConfigureationV2Binding
import com.yunshang.haile_manager_android.databinding.ItemDeviceFuncConfigurationDryerTimeBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceFuncConfigurationWashingBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.visibility
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog

class DeviceFunConfigurationV2Activity :
    BaseBusinessActivity<ActivityDeviceFunConfigurationV2Binding, DeviceFunConfigurationV2ViewModel>(
        DeviceFunConfigurationV2ViewModel::class.java, BR.vm
    ) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemDeviceFunConfigureationV2Binding, SkuFunConfigurationV2Param>(
            R.layout.item_device_fun_configureation_v2, BR.item
        ) { mItemBinding, pos, item ->
            val itemDryerHeight = DimensionUtils.dip2px(this, 54f)

            mItemBinding?.tvDeviceFunConfigurationIndex?.text =
                StringUtils.getString(R.string.device_func_configuration_title, pos + 1)

            mViewModel.isWashingOrShoesOrDryer.observe(this) {
                mItemBinding?.itemDeviceFunConfigurationDesc?.visibility(true == it)
            }
            mViewModel.isWashingOrShoes.observe(this) {
                mItemBinding?.itemDeviceFunConfigurationAttr?.visibility(true != it)
                mItemBinding?.switchDeviceFunConfigurationAttrDefault?.visibility(true == it)
                mItemBinding?.ivDeviceFunConfigurationAttrDefaultRight?.visibility(false == it)

                if (true == it) {
                    // 洗衣机或洗鞋机
                    mItemBinding?.llDeviceFunConfigurationAttrSku?.layoutId =
                        R.layout.item_device_func_configuration_washing

                    mItemBinding?.llDeviceFunConfigurationAttrSku?.buildChild<ItemDeviceFuncConfigurationWashingBinding, ExtAttrDtoItem>(
                        item.selectExtAttr,
                        LinearLayoutCompat.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                        )
                    ) { _, childBinding, data ->
                        childBinding.item = data
                        // 修改单位
                        if (1 == data.functionType) {
                            childBinding.itemDeviceFunConfigurationWashTimeSelect.onSelectedEvent =
                                {
                                    showTimeDialog(
                                        item,
                                        true,
                                        pos
                                    )
                                }
                        } else {
                            childBinding.itemDeviceFunConfigurationWashTime.mTailView.text =
                                data.getUnit()
                        }

                        // 固定价，不可输入
                        (1 != data.priceType).let { canUpdate ->
                            childBinding.itemDeviceFunConfigurationWashMoney.contentView.isFocusable =
                                canUpdate
                        }

                        childBinding.itemDeviceFunConfigurationWashTime.mTitleView.gravity =
                            Gravity.START or Gravity.CENTER_VERTICAL
                        childBinding.itemDeviceFunConfigurationWashMoney.mTitleView.gravity =
                            Gravity.START or Gravity.CENTER_VERTICAL
                        childBinding.itemDeviceFunConfigurationPulseCount.visibility(
                            DeviceCategory.isPulseDevice(
                                mViewModel.communicationType
                            )
                        )
                    }

                    mItemBinding?.switchDeviceFunConfigurationAttrDefault?.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            mViewModel.configureList.value?.let { list ->
                                list.forEach { sku ->
                                    sku.selectExtAttr.firstOrNull()?.let { first ->
                                        first.isDefault = (sku.skuId == item.skuId)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    mItemBinding?.itemDeviceFunConfigurationAttr?.onSelectedEvent = {
                        showTimeDialog(
                            item,
                            false,
                            pos
                        )
                    }

                    mItemBinding?.llDeviceFunConfigurationAttrSku?.layoutId =
                        R.layout.item_device_func_configuration_dryer_time
                    mItemBinding?.llDeviceFunConfigurationAttrSku?.buildChild<ItemDeviceFuncConfigurationDryerTimeBinding, ExtAttrDtoItem>(
                        item.selectExtAttr,
                        LinearLayoutCompat.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, itemDryerHeight
                        )
                    ) { _, childBinding, data ->
                        childBinding.item = data
                        (1 != data.priceType).let { canUpdate ->
                            childBinding.etDryerPrice.isFocusable = canUpdate
                        }
                        childBinding.etDryerPulse.visibility(
                            DeviceCategory.isPulseDevice(
                                mViewModel.communicationType
                            )
                        )
                    }
                    mItemBinding?.llDeviceFunConfigurationAttrDefault?.setOnClickListener {
                        showDefaultDialog(item, pos)
                    }
                }
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_device_fun_configuration_v2

    override fun initIntent() {
        super.initIntent()
        mViewModel.spuId = IntentParams.DeviceFunConfigurationV2Params.parseSpuId(intent)
        mViewModel.categoryCode.value = IntentParams.DeviceParams.parseCategoryCode(intent)
        mViewModel.communicationType = IntentParams.DeviceParams.parseCommunicationType(intent)
        mViewModel.spuExtAttrDto =
            IntentParams.DeviceFunConfigurationV2Params.parseExtAttrDto(intent)
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.configureList.observe(this) {
            it?.let {
                mAdapter.refreshList(it, true)
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.itemDeviceFunConfigurationPriceModel.onSelectedEvent = {
            CommonBottomSheetDialog.Builder(
                StringUtils.getString(R.string.pricing_manner), mViewModel.priceModelList
            ).apply {
                onValueSureListener = object :
                    CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                    override fun onValue(data: SearchSelectParam?) {
                        data?.let {
                            mViewModel.priceModel.value = data
                        }
                    }
                }
            }.build().show(supportFragmentManager)
        }
        mBinding.itemDeviceFunConfigurationCalculateModel.onSelectedEvent = {
            CommonBottomSheetDialog.Builder(
                StringUtils.getString(R.string.pricing_manner), mViewModel.calculateModelList
            ).apply {
                onValueSureListener = object :
                    CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                    override fun onValue(data: SearchSelectParam?) {
                        data?.let {
                            mViewModel.calculateModel.value = data
                        }
                    }
                }
            }.build().show(supportFragmentManager)
        }

        mBinding.rvDeviceFunConfigurationList.layoutManager = LinearLayoutManager(this)
        ContextCompat.getDrawable(
            this,
            R.drawable.divder_f7f7f7_size8
        )?.let {
            mBinding.rvDeviceFunConfigurationList.addItemDecoration(
                DividerItemDecoration(
                    this,
                    DividerItemDecoration.VERTICAL
                ).apply { setDrawable(it) })
        }
        mBinding.rvDeviceFunConfigurationList.adapter = mAdapter
//        // 保存
//        mBinding.btnDeviceCreateSubmit.setOnClickListener {
//            SoftKeyboardUtils.hideShowKeyboard(it)
//            mViewModel.save()
//        }
    }

    /**
     * 显示烘干机时间选择弹窗
     * @param exts 时间列表
     * @param pos item position
     */
    private fun showTimeDialog(configure: SkuFunConfigurationV2Param, isSingle: Boolean, pos: Int) {
        val list = configure.extAttrDto.items.filter { item -> item.isOn }
        if (1 == list.size) {
            SToast.showToast(this, "当前仅有一个配置项")
            return
        }

        MultiSelectBottomSheetDialog.Builder(
            StringUtils.getString(R.string.configure) + if (isSingle) "" else "（可多选）",
            list,
        ).apply {
            supportSingle = isSingle
            onValueSureListener = object :
                MultiSelectBottomSheetDialog.OnValueSureListener<ExtAttrDtoItem> {
                override fun onValue(
                    selectList: List<ExtAttrDtoItem>,
                    allSelectData: List<ExtAttrDtoItem>
                ) {
                    configure.selectExtAttr = allSelectData.intersect(selectList).toList()
                    mAdapter.notifyItemChanged(pos)
                }
            }
        }.build().show(supportFragmentManager)
    }

    /**
     * 显示默认选择弹窗
     * @param exts 时间列表
     * @param pos item position
     */
    private fun showDefaultDialog(configure: SkuFunConfigurationV2Param, pos: Int) {
        // 避免数据污染
        val temp = GsonUtils.json2List(
            GsonUtils.any2Json(configure.selectExtAttr),
            ExtAttrDtoItem::class.java
        ) ?: listOf()
        temp.forEach { item -> item.isCheck = false }

        MultiSelectBottomSheetDialog.Builder(
            StringUtils.getString(R.string.configure),
            temp,
        ).apply {
            supportSingle = true
            onValueSureListener = object :
                MultiSelectBottomSheetDialog.OnValueSureListener<ExtAttrDtoItem> {
                override fun onValue(
                    selectList: List<ExtAttrDtoItem>,
                    allSelectData: List<ExtAttrDtoItem>
                ) {
                    selectList.firstOrNull()?.let { first ->
                        configure.selectExtAttr.forEach { attr ->
                            attr.isDefault = (first.unitAmount == attr.unitAmount)
                        }
                    }
                    mAdapter.notifyItemChanged(pos)
                }
            }
        }.build().show(supportFragmentManager)
    }

    override fun initData() {
        mViewModel.requestData()
    }
}