package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.SoftKeyboardUtils
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
                val isPulseDevice = DeviceCategory.isPulseDevice(mViewModel.communicationType)
                if (true == it) {
                    // 洗衣机或洗鞋机
                    mItemBinding?.llDeviceFunConfigurationAttrSku?.layoutId =
                        R.layout.item_device_func_configuration_washing
                    if (item.selectExtAttr.isNotEmpty()) {
                        mItemBinding?.llDeviceFunConfigurationAttrSku?.buildChild<ItemDeviceFuncConfigurationWashingBinding, ExtAttrDtoItem>(
                            item.selectExtAttr.subList(0, 1),
                            LinearLayoutCompat.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                            )
                        ) { _, childBinding, data ->
                            childBinding.item = data
                            // 修改单位
                            (1 != data.functionType).let { canUpdate ->
                                childBinding.itemDeviceFunConfigurationWashTime.contentView.isFocusable =
                                    canUpdate
                                childBinding.itemDeviceFunConfigurationWashTime.contentView.setTextColor(
                                    ContextCompat.getColor(
                                        this,
                                        if (canUpdate) R.color.common_txt_color else R.color.common_txt_hint_color
                                    )
                                )
                                childBinding.itemDeviceFunConfigurationWashTime.contentView.setOnClickListener {
                                    SToast.showToast(this, "固定配置不可修改")
                                }
                            }
                            childBinding.itemDeviceFunConfigurationWashTime.mTailView.text =
                                data.getUnit()

                            // 固定价，不可输入
                            (1 != data.priceType).let { canUpdate ->
                                childBinding.itemDeviceFunConfigurationWashMoney.contentView.isFocusable =
                                    canUpdate
                                childBinding.itemDeviceFunConfigurationWashMoney.contentView.setTextColor(
                                    ContextCompat.getColor(
                                        this,
                                        if (canUpdate) R.color.common_txt_color else R.color.common_txt_hint_color
                                    )
                                )
                                childBinding.itemDeviceFunConfigurationWashMoney.contentView.setOnClickListener {
                                    SToast.showToast(this, "固定配置不可修改")
                                }
                            }

                            childBinding.itemDeviceFunConfigurationWashTime.mTitleView.gravity =
                                Gravity.START or Gravity.CENTER_VERTICAL
                            childBinding.itemDeviceFunConfigurationWashMoney.mTitleView.gravity =
                                Gravity.START or Gravity.CENTER_VERTICAL
                            childBinding.itemDeviceFunConfigurationPulseCount.visibility(
                                isPulseDevice
                            )
                        }
                    }

                    // 默认选中事件
                    mItemBinding?.switchDeviceFunConfigurationAttrDefault?.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            mViewModel.dealConfigureList.value?.let { list ->
                                list.forEach { sku ->
                                    sku.defaultOpen = (sku.skuId == item.skuId)
                                }
                            }
                        }
                    }
                    mItemBinding?.tvDeviceFunConfigurationAttrDefault?.visibility = View.INVISIBLE
                } else {
                    mItemBinding?.itemDeviceFunConfigurationAttr?.onSelectedEvent = {
                        showTimeDialog(
                            item,
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
                            childBinding.etDryerPrice.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    if (canUpdate) R.color.common_txt_color else R.color.common_txt_hint_color
                                )
                            )
                            childBinding.etDryerPrice.setOnClickListener {
                                SToast.showToast(this, "固定配置不可修改")
                            }
                        }
                        childBinding.etDryerPulse.visibility(isPulseDevice)
                        childBinding.tvDryerPulseHint.visibility(isPulseDevice)
                    }

                    // 默认选中事件
                    mItemBinding?.llDeviceFunConfigurationAttrDefault?.setOnClickListener {
                        showDefaultDialog(item)
                    }
                    mItemBinding?.tvDeviceFunConfigurationAttrDefault?.visibility = View.VISIBLE
                }
            }

            // 固定价才显示默认选中
            mViewModel.selectPriceModel.observe(this) {
                mItemBinding?.llDeviceFunConfigurationAttrDefault?.visibility(1 == it.id)
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_device_fun_configuration_v2

    override fun backBtn(): View = mBinding.barDeviceFuncConfigurationTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.goodId = IntentParams.DeviceFunConfigurationV2Params.parseGoodId(intent)
        mViewModel.spuId = IntentParams.DeviceFunConfigurationV2Params.parseSpuId(intent)
        mViewModel.categoryCode.value = IntentParams.DeviceParams.parseCategoryCode(intent)
        mViewModel.communicationType = IntentParams.DeviceParams.parseCommunicationType(intent)
        mViewModel.spuExtAttrDto.value =
            IntentParams.DeviceFunConfigurationV2Params.parseExtAttrDto(intent)
        mViewModel.oldConfigureList =
            IntentParams.DeviceFunConfigurationV2Params.parseSkuExtAttrDto(intent)
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.spuExtAttrDto.observe(this) {
            mViewModel.initSelectPriceModel(it)
            mViewModel.initSelectCalculateModel(it)
        }

        mViewModel.dealConfigureList.observe(this) {
            mAdapter.refreshList(it, true)
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
                            mViewModel.selectPriceModel.value = data
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
                            mViewModel.selectCalculateModel.value = data
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
        // 保存
        mBinding.btnDeviceCreateSubmit.setOnClickListener {
            SoftKeyboardUtils.hideShowKeyboard(it)
            mViewModel.save(this) { json ->
                json?.let {
                    setResult(
                        IntentParams.DeviceFunConfigurationV2Params.ResultCode,
                        Intent().apply {
                            putExtras(IntentParams.DeviceFunConfigurationV2Params.packResult(it))
                        }
                    )
                }
                finish()
            }
        }
    }

    /**
     * 显示烘干机时间选择弹窗
     * @param exts 时间列表
     * @param pos item position
     */
    private fun showTimeDialog(configure: SkuFunConfigurationV2Param, pos: Int) {
        configure.extAttrDto.items?.let {
            val list = it.filter { item -> item.isOn }
            if (1 == list.size) {
                SToast.showToast(this, "当前仅有一个配置项")
                return
            }

            MultiSelectBottomSheetDialog.Builder(
                StringUtils.getString(R.string.configure) + "（可多选）",
                list,
            ).apply {
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
    }

    /**
     * 显示默认选择弹窗
     * @param exts 时间列表
     * @param pos item position
     */
    private fun showDefaultDialog(configure: SkuFunConfigurationV2Param) {
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
                            if (first.id == attr.id) {
                                attr.isDefault = true
                                configure.defaultUnitAmount = attr.getUnit()
                            } else {
                                attr.isDefault = false
                            }
                        }
                    }
                }
            }
        }.build().show(supportFragmentManager)
    }

    override fun initData() {
        mViewModel.requestData()
    }
}