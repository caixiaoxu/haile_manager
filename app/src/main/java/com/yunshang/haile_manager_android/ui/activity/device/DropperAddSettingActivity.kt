package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceMultiChangeViewModel
import com.yunshang.haile_manager_android.business.vm.DropperAddSettingViewModel
import com.yunshang.haile_manager_android.data.entities.DosingConfigs
import com.yunshang.haile_manager_android.data.entities.SkuEntity
import com.yunshang.haile_manager_android.databinding.ActivityDropperAddSettingBinding
import com.yunshang.haile_manager_android.databinding.ItemDisposeUseDryerBinding
import com.yunshang.haile_manager_android.databinding.ItemDisposeUseItemBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.personal.IncomeActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.web.WebViewActivity

class DropperAddSettingActivity :
    BaseBusinessActivity<ActivityDropperAddSettingBinding, DropperAddSettingViewModel>(
        DropperAddSettingViewModel::class.java, BR.vm
    ) {

    companion object {
        const val Deviceid = "id"
        const val SpuId = "spuId"
        const val ResultCode = 0x90004
        const val ResultData = "ResultData"
    }


    override fun layoutId(): Int = R.layout.activity_dropper_add_setting

    override fun backBtn(): View = mBinding.barDeviceTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.spuId = intent.getIntExtra(SpuId, -1)
        mViewModel.goodsId = intent.getIntExtra(Deviceid, -1)

        //刷新数据
        mViewModel.configurationList.observe(this) {
            mAdapter.refreshList(it, true)
        }
        mViewModel.jump.observe(this) {
            finish()
        }
        mViewModel.resultData.observe(this) {
            setResult(ResultCode, Intent().apply {
                putExtra(ResultData, GsonUtils.any2Json(it))
            })
            finish()
        }
    }

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemDisposeUseDryerBinding, SkuEntity>(
            R.layout.item_dispose_use_dryer, BR.item
        ) { mBinding, postop, items ->
            mBinding?.itemRecyclerView?.layoutManager = LinearLayoutManager(this)
            var adas = CommonRecyclerAdapter<ItemDisposeUseItemBinding, DosingConfigs>(
                R.layout.item_dispose_use_item, BR.item
            ) { mBinding, pos, item ->
                mBinding?.tvName?.text = "单次用量 ${item.amount}ml/${item.price}元"
                mBinding?.tvDefalt?.visibility = if (item.isDefault) View.VISIBLE else View.GONE
                mBinding?.tvState?.text = if (item.isOn) "启用中" else "已停用"
                mBinding?.root?.setOnClickListener {
                    startNext.launch(Intent(
                        this@DropperAddSettingActivity,
                        DropperAllocationActivity::class.java
                    ).apply {
                        putExtra(DropperAllocationActivity.Amount, item.amount.toString())
                        putExtra(DropperAllocationActivity.liquidType, item.liquidTypeId)
                        putExtra(DropperAllocationActivity.Price, item.price.toString())
                        putExtra(DropperAllocationActivity.isDefault, item.isDefault)
                        putExtra(DropperAllocationActivity.isOn, item.isOn)
                        putExtra(
                            DropperAllocationActivity.itemId,
                            "${postop}-${pos}"
                        )
                    })
                }
            }
            mBinding?.itemRecyclerView?.adapter = adas
            adas.refreshList(items.dosingConfigs.toMutableList(), true)
            mBinding?.tvSetting?.setOnClickListener {
                startNext.launch(Intent(
                    this@DropperAddSettingActivity, DropperAllocationActivity::class.java
                ).apply {
                    putExtra(
                        DropperAllocationActivity.itemId,
                        postop.toString()
                    )
                    putExtra(
                        DropperAllocationActivity.liquidType,
                        items.dosingConfigs[0].liquidTypeId
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
                        var itemcode = intent.getStringExtra(DropperAllocationActivity.itemId)
                        var amount = intent.getStringExtra(DropperAllocationActivity.Amount)
                        var price = intent.getStringExtra(DropperAllocationActivity.Price)
                        var liquidType = intent.getIntExtra(DropperAllocationActivity.liquidType, 0)
                        var ison = intent.getBooleanExtra(DropperAllocationActivity.isOn, false)
                        var isdefault =
                            intent.getBooleanExtra(DropperAllocationActivity.isDefault, false)
                        var dosing = DosingConfigs(
                            amount = amount?.toInt() ?: 0,
                            itemId = 0,
                            liquidTypeId = liquidType,
                            liquidType = liquidType,
                            price = price?.toInt() ?: 0,
                            name = if (liquidType == 1) "洗衣液" else "除菌液",
                            isDefault = isdefault,
                            isOn = ison
                        )
                        var skulists = ArrayList<SkuEntity>()
                        var skus_ = ArrayList<DosingConfigs>()
                        skulists.addAll(mViewModel.configurationList.value!!)
                        if (itemcode!!.contains("-")) {
                            skus_.addAll(skulists[itemcode.split("-")[0].toInt()].dosingConfigs)
                            if (isdefault) {
                                skus_.forEach {
                                    it.isDefault = false
                                }
                            }
                            skus_[itemcode.split("-")[1].toInt()] = dosing
                            skulists[itemcode.split("-")[0].toInt()].dosingConfigs = skus_
                        } else {
                            skus_.addAll(skulists[itemcode.toInt()].dosingConfigs)
                            if (isdefault) {
                                skus_.forEach {
                                    it.isDefault = false
                                }
                            }
                            skus_.add(dosing)
                            skulists[itemcode.toInt()].dosingConfigs = skus_
                        }
                        mViewModel.configurationList.postValue(skulists)
                    }
                }
            }
        }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerView.adapter = mAdapter
    }

    override fun initData() {
        mViewModel.requestData()
    }
}