package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.ShopPositionCreateViewModel
import com.yunshang.haile_manager_android.data.arguments.BusinessHourEntity
import com.yunshang.haile_manager_android.data.arguments.BusinessHourParams
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.extend.hasVal
import com.yunshang.haile_manager_android.databinding.ActivityShopPositionCreateBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.visibility
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog

class ShopPositionCreateActivity :
    BaseBusinessActivity<ActivityShopPositionCreateBinding, ShopPositionCreateViewModel>(
        ShopPositionCreateViewModel::class.java, BR.vm
    ) {

    // 搜索界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                IntentParams.SearchSelectTypeParam.ShopResultCode -> {
                    it.data?.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)
                        ?.let { json ->
                            GsonUtils.json2List(json, SearchSelectParam::class.java)
                                ?.firstOrNull()?.let { first ->
                                    mViewModel.positionParam.value?.changeShop(first.id, first.name)
                                }
                        }
                }
                IntentParams.LocationParams.LocationResultCode -> {
                    it.data?.let { intent ->
                        IntentParams.LocationParams.parseLocationResultData(intent)
                            ?.let { poiItem ->
                                mViewModel.positionParam.value?.changeAddress(
                                    poiItem.latitude,
                                    poiItem.longitude,
                                    poiItem.address
                                )

                            }
                    }
                }
                IntentParams.ShopBusinessHoursParams.ResultCode -> {
                    it.data?.let { intent ->
                        IntentParams.ShopBusinessHoursParams.parseShopBusinessHoursJson(intent)
                            ?.let { hours ->
                                mViewModel.positionParam.value?.changeWorkTime(
                                    GsonUtils.json2List(
                                        hours,
                                        BusinessHourEntity::class.java
                                    )
                                )
                            }
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_shop_position_create

    override fun backBtn(): View = mBinding.barAddPtTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()

        val shopId = IntentParams.ShopParams.parseShopId(intent)
        val shopName = IntentParams.ShopParams.parseShopName(intent)
        if (-1 != shopId && null != shopName) {
            mViewModel.positionParam.value?.changeShop(shopId, shopName)
        }
        mViewModel.updatePositionDetail(
            IntentParams.ShopPositionCreateParams.parseShopPositionDetail(
                intent
            )
        )
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        // 所属门店
        if (null == mViewModel.positionParam.value?.id) {
            mBinding.itemPositionShop.onSelectedEvent = {
                startSearchSelect.launch(
                    Intent(
                        this@ShopPositionCreateActivity,
                        SearchSelectRadioActivity::class.java
                    ).apply {
                        putExtras(
                            IntentParams.SearchSelectTypeParam.pack(
                                IntentParams.SearchSelectTypeParam.SearchSelectTypeShop,
                            )
                        )
                    }
                )
            }
        } else {
            mBinding.itemPositionShop.contentView.isEnabled = false
            mBinding.itemPositionShop.contentView.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.common_sub_txt_color
                )
            )
            mBinding.itemPositionShop.mTailView.visibility(false)
        }

        //定位
        mBinding.itemPositionLocation.onSelectedEvent = {
            if (null != mViewModel.positionParam.value?.shopId) {
                mViewModel.requestShopDetail {
                    it?.let { cityName ->
                        startSearchSelect.launch(
                            Intent(
                                this@ShopPositionCreateActivity,
                                CurLocationSelectorActivity::class.java
                            ).apply {
                                putExtras(
                                    IntentParams.LocationSelectParams.packCity(cityName)
                                )
                            }
                        )
                    } ?: SToast.showToast(this, "无法获取门店所在城市")
                }
            } else {
                SToast.showToast(this, "无法获取门店所在城市")
            }
        }

        //性别
        mBinding.itemPositionSex.onSelectedEvent = {
            val selectList = StringUtils.getStringArray(R.array.sex_list).mapIndexed { index, txt ->
                SearchSelectParam(index, txt)
            }
            val select =
                selectList[if (mViewModel.positionParam.value?.sex.hasVal()) mViewModel.positionParam.value!!.sex!! else 2]
            CommonBottomSheetDialog.Builder(
                getString(R.string.apply_sex), selectList
            ).apply {
                mustSelect = false
                selectData = select
                onValueSureListener =
                    object : CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                        override fun onValue(data: SearchSelectParam?) {
                            mViewModel.positionParam.value?.sexVal = data?.id
                        }
                    }
            }.build().show(supportFragmentManager)
        }

        // 营业时间
        mBinding.itemPositionBusinessHours.onSelectedEvent = {
            startSearchSelect.launch(
                Intent(
                    this@ShopPositionCreateActivity,
                    ShopBusinessHoursActivity::class.java
                ).apply {
                    GsonUtils.json2List(
                        mViewModel.positionParam.value?.workTimeStr,
                        BusinessHourParams::class.java
                    )?.let { params ->
                        putExtras(IntentParams.ShopBusinessHoursParams.pack(params.map {
                            BusinessHourEntity().apply {
                                formatData(it.weekDays, it.workTime)
                            }
                        }.toMutableList()))
                    }
                })
        }
    }

    override fun initData() {
    }
}