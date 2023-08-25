package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DropperAddSettingViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.DosingConfigs
import com.yunshang.haile_manager_android.data.entities.SkuEntity
import com.yunshang.haile_manager_android.databinding.ActivityDropperAddSettingBinding
import com.yunshang.haile_manager_android.databinding.ItemDisposeUseDryerBinding
import com.yunshang.haile_manager_android.databinding.ItemDisposeUseItemBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter

class DropperAddSettingActivity :
    BaseBusinessActivity<ActivityDropperAddSettingBinding, DropperAddSettingViewModel>(
        DropperAddSettingViewModel::class.java, BR.vm
    ) {

    companion object {
        const val Deviceid = "id"
        const val SpuId = "spuId"
        const val ResultCode = 0x90004
        const val ResultData = "ResultData"
        const val OldFuncConfiguration = "oldFuncConfiguration"
    }

    override fun layoutId(): Int = R.layout.activity_dropper_add_setting

    override fun backBtn(): View = mBinding.barDeviceDispenserFunConfigurationTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.spuId = intent.getIntExtra(SpuId, -1)
        mViewModel.goodsId = intent.getIntExtra(Deviceid, -1)
    }

    override fun initEvent() {
        super.initEvent()

        //刷新数据
        mViewModel.configurationList.observe(this) {
            mAdapter.refreshList(it, true)
        }
        mViewModel.jump.observe(this) {
            finish()
        }
        mViewModel.resultData.observe(this) {
            setResult(IntentParams.DeviceFunctionConfigurationParams.ResultCode, Intent().apply {
                putExtras(IntentParams.DeviceFunctionConfigurationParams.packResult(it))
            })
            finish()
        }
    }

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemDisposeUseDryerBinding, SkuEntity>(
            R.layout.item_dispose_use_dryer, BR.item
        ) { mItemBinding, posTop, items ->
            mItemBinding?.rvDispenserFunConfigItems?.layoutManager = LinearLayoutManager(this)
            mItemBinding?.rvDispenserFunConfigItems?.adapter =
                CommonRecyclerAdapter<ItemDisposeUseItemBinding, DosingConfigs>(
                    R.layout.item_dispose_use_item, BR.item
                ) { mChildBinding, pos, item ->
                    mChildBinding?.root?.setOnClickListener {
                        startNext.launch(Intent(
                            this@DropperAddSettingActivity,
                            DropperAllocationActivity::class.java
                        ).apply {
                            putExtra(DropperAllocationActivity.Amount, item.amount.toString())
                            putExtra(DropperAllocationActivity.liquidType, item.liquidTypeId)
                            putExtra(DropperAllocationActivity.Price, item.price.toString())
                            putExtra(DropperAllocationActivity.isDefault, item.isDefault)
                            putExtra(DropperAllocationActivity.isOn, item.isOn)
                            putExtra("number", "${posTop}-${pos}")
                            putExtra(
                                DropperAllocationActivity.itemId, "${posTop}-${pos}"
                            )
                        })
                    }
                }.apply {
                    refreshList(items.dosingConfigs.toMutableList(), true)
                }
            mItemBinding?.tvDispenserFunConfigAdd?.setOnClickListener {
                if (items.dosingConfigs.size == 3) {
                    SToast.showToast(this@DropperAddSettingActivity, "最多增加三条配置")
                    return@setOnClickListener
                }
                startNext.launch(Intent(
                    this@DropperAddSettingActivity, DropperAllocationActivity::class.java
                ).apply {
                    putExtra(
                        DropperAllocationActivity.itemId, posTop.toString()
                    )
                    putExtra(
                        DropperAllocationActivity.liquidType,
                        if (items.name == "洗衣液") 1 else 2
                    )
                })
            }
        }
    }

    private val startNext =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    result.data?.let { intent ->
                        val itemCode = intent.getStringExtra(DropperAllocationActivity.itemId)
                        val amount = intent.getStringExtra(DropperAllocationActivity.Amount)
                        val price = intent.getStringExtra(DropperAllocationActivity.Price)
                        val liquidType = intent.getIntExtra(DropperAllocationActivity.liquidType, 0)
                        val isOn = intent.getBooleanExtra(DropperAllocationActivity.isOn, false)
                        val isDefault =
                            intent.getBooleanExtra(DropperAllocationActivity.isDefault, false)
                        val dosing = DosingConfigs(
                            amount = amount?.toInt() ?: 0,
                            itemId = 0,
                            liquidTypeId = liquidType,
                            liquidType = liquidType,
                            price = price?.toDouble() ?: 0.0,
                            name = if (liquidType == 1) "洗衣液" else "除菌液",
                            isDefault = isDefault,
                            isOn = isOn
                        )
                        val skuLists = ArrayList<SkuEntity>()
                        skuLists.addAll(mViewModel.configurationList.value!!)
                        val skus = ArrayList<DosingConfigs>()
                        if (itemCode!!.contains("-")) {
                            skus.addAll(skuLists[itemCode.split("-")[0].toInt()].dosingConfigs)
                            if (isDefault) {
                                skus.forEach {
                                    it.isDefault = false
                                }
                            }
                            skus[itemCode.split("-")[1].toInt()] = dosing
                            skuLists[itemCode.split("-")[0].toInt()].dosingConfigs = skus
                        } else {
                            skus.addAll(skuLists[itemCode.toInt()].dosingConfigs)
                            if (isDefault) {
                                skus.forEach {
                                    it.isDefault = false
                                }
                            }
                            skus.add(dosing)
                            skuLists[itemCode.toInt()].dosingConfigs = skus
                        }
                        mViewModel.configurationList.postValue(skuLists)
                    }
                }

                DropperAllocationActivity.ResultCode -> {
                    result.data?.let { intent ->
                        val number = intent.getStringExtra("number")
                        val skuLists = ArrayList<SkuEntity>()
                        val skus = ArrayList<DosingConfigs>()
                        skuLists.addAll(mViewModel.configurationList.value!!)
                        if (number!!.contains("-")) {
                            skus.addAll(skuLists[number.split("-")[0].toInt()].dosingConfigs)
                            skus.removeAt(number.split("-")[1].toInt())
                            skuLists[number.split("-")[0].toInt()].dosingConfigs = skus
                            mViewModel.configurationList.postValue(skuLists)
                        }
                    }
                }

            }
        }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.rvDeviceDispenserFunConfigurationList.layoutManager = LinearLayoutManager(this)
        ResourcesCompat.getDrawable(resources, R.drawable.divide_size8, null)?.let {
            mBinding.rvDeviceDispenserFunConfigurationList.addItemDecoration(
                DividerItemDecoration(
                    this@DropperAddSettingActivity,
                    DividerItemDecoration.VERTICAL
                ).apply {
                    setDrawable(it)
                })
        }
        mBinding.rvDeviceDispenserFunConfigurationList.adapter = mAdapter
    }

    override fun initData() {
        if (-1 == mViewModel.goodsId) {
            mViewModel.requestData()
        } else {
            mViewModel.useOldData(intent.getStringExtra(OldFuncConfiguration))
        }
    }
}