package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.ViewUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.ShopPositionCreateViewModel
import com.yunshang.haile_manager_android.data.arguments.*
import com.yunshang.haile_manager_android.data.extend.hasVal
import com.yunshang.haile_manager_android.databinding.ActivityShopPositionCreateBinding
import com.yunshang.haile_manager_android.databinding.ItemShopPositionCreateBusinessPhoneBinding
import com.yunshang.haile_manager_android.databinding.ItemShopPositionCreatePositionBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.PositionCreateSheetDialog

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
        mViewModel.positionParam.observe(this) {
            buildPositionListView()
            buildContactListView()
        }
        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        // 所属门店
        mBinding.clShopPositionCreateShop.setOnClickListener {
            if (!ViewUtils.isFastDoubleClick()) {
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
        }

        // 营业点
        mBinding.ibShopPositionCreateAddPt.setOnClickListener {
            if (mViewModel.positionParam.value?.nameAndFloorList?.let { it.size < 20 } != false) {
                showAddPositionDialog(-1)
            } else {
                SToast.showToast(this@ShopPositionCreateActivity, "营业点最多添加2个")
            }
        }

        //定位
        mBinding.clShopPositionCreateLocation.setOnClickListener {
            if (!ViewUtils.isFastDoubleClick()) {
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
                    SToast.showToast(this, "请先选择所属门店")
                }
            }
        }

        //性别
        mBinding.clShopPositionCreateSex.setOnClickListener {
            if (!ViewUtils.isFastDoubleClick()) {
                val selectList =
                    StringUtils.getStringArray(R.array.sex_list).mapIndexed { index, txt ->
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
        }

        // 营业时间
        mBinding.clShopPositionCreateBusinessHours.setOnClickListener {
            if (!ViewUtils.isFastDoubleClick()) {
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

        // 联系电话
        mBinding.ibShopPositionCreateBusinessPhoneAdd.setOnClickListener {
            if (mViewModel.positionParam.value?.serviceTelephone?.split(",")
                    ?.let { it.size < 3 } != false
            ) {
                showContactPhoneDialog(-1)
            } else {
                SToast.showToast(this@ShopPositionCreateActivity, "客服电话最多添加2个")
            }
        }
    }

    /**
     * 显示添加营业点弹窗
     * @param index 索引
     * @param position 营业点信息
     */
    private fun showAddPositionDialog(index: Int, position: NameAndFloor? = null) {
        PositionCreateSheetDialog.Builder(0) { value1, value2 ->
            mViewModel.positionParam.value?.nameAndFloorList?.let { list ->
                val pot = NameAndFloor(value1, value2)
                if (-1 == index) list.add(pot)
                else list[index] = pot
            }
            buildPositionListView()
        }.apply {
            position?.name?.let { name ->
                positionName.value = name
            }
            position?.floorCode?.let { floor ->
                positionFloor.value = floor
            }
        }.build().show(supportFragmentManager)
    }

    /**
     * 显示联系人弹窗
     * @param index 索引
     * @param phone 电话号
     */
    private fun showContactPhoneDialog(index: Int, phone: String? = null) {
        PositionCreateSheetDialog.Builder(1) { value1, _ ->
            if (!value1.isNullOrEmpty()) {
                val phoneList = mViewModel.positionParam.value?.serviceTelephone?.split(",")
                    ?.toMutableList() ?: mutableListOf()
                if (-1 == index) phoneList.add(value1)
                else phoneList[index] = value1
                mViewModel.positionParam.value?.serviceTelephone = phoneList.joinToString(",")
                buildContactListView()
            }
        }.apply {
            phone?.let {
                contactPhone.value = phone
            }
        }.build().show(supportFragmentManager)
    }

    /**
     * 构建营业点列表布局
     */
    private fun buildPositionListView() {
        mBinding.llShopPositionCreateAddPt.buildChild<ItemShopPositionCreatePositionBinding, NameAndFloor>(
            mViewModel.positionParam.value?.nameAndFloorList?.toList()
        ) { index, childBinding, data ->
            childBinding.child = data
            childBinding.ibShopPositionCreatePositionDel.setOnClickListener {
                mViewModel.positionParam.value?.nameAndFloorList?.removeAt(index)
                mBinding.llShopPositionCreateAddPt.removeViewAt(index)
            }
        }
    }

    /**
     * 构建联系人电话列表布局
     */
    private fun buildContactListView() {
        mBinding.llShopPositionCreateBusinessPhone.buildChild<ItemShopPositionCreateBusinessPhoneBinding, String>(
            mViewModel.positionParam.value?.serviceTelephone?.split(",")
        ) { index, childBinding, data ->
            childBinding.phone = data
            childBinding.tvShopPositionCreateBusinessPhoneUpdate.setOnClickListener {
                showContactPhoneDialog(index, data)
            }
            childBinding.ibShopPositionCreateBusinessPhoneDel.setOnClickListener {
                val phoneList = mViewModel.positionParam.value?.serviceTelephone?.split(",")
                    ?.toMutableList() ?: mutableListOf()
                phoneList.removeAt(index)
                mViewModel.positionParam.value?.serviceTelephone = phoneList.joinToString(",")
                mBinding.llShopPositionCreateBusinessPhone.removeViewAt(index)
            }
        }
    }

    override fun initData() {
    }
}