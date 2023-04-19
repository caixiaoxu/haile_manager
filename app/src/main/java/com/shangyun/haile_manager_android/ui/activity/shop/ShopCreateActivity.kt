package com.shangyun.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.amap.api.services.core.PoiItem
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.SearchSelectViewModel.Companion.SCHOOL
import com.shangyun.haile_manager_android.business.vm.SearchSelectViewModel.Companion.SEARCH_TYPE
import com.shangyun.haile_manager_android.business.vm.ShopCreateViewModel
import com.shangyun.haile_manager_android.data.entities.SchoolSelectEntity
import com.shangyun.haile_manager_android.data.entities.ShopBusinessTypeEntity
import com.shangyun.haile_manager_android.data.entities.ShopTypeEntity
import com.shangyun.haile_manager_android.databinding.ActivityShopCreateBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.view.dialog.AreaSelectDialog
import com.shangyun.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.shangyun.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog
import com.shangyun.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.shangyun.haile_manager_android.utils.DateTimeUtils
import com.shangyun.haile_manager_android.utils.SoftHideKeyBoardUtil
import timber.log.Timber
import java.util.*

class ShopCreateActivity :
    BaseBusinessActivity<ActivityShopCreateBinding, ShopCreateViewModel>() {

    companion object {
        const val SchoolResultCode = 10001
        const val SchoolResultData = "SchoolResultData"
        const val LocationResultCode = 10002
        const val LocationResultData = "LocationResultData"
    }

    // 店铺类型
    private var mShopTypeDialog: CommonBottomSheetDialog<ShopTypeEntity>? = null

    // 搜索界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                SchoolResultCode -> {
                    it.data?.getStringExtra(SchoolResultData)?.let { json ->
                        GsonUtils.json2Class(json, SchoolSelectEntity::class.java)?.let { school ->
                            mShopCreateViewModel.changeSchool(school)
                        }
                    }
                }
                LocationResultCode -> {
                    it.data?.getStringExtra(LocationResultData)?.let { json ->
                        GsonUtils.json2Class(json, PoiItem::class.java)?.let { poiItem ->
                            mShopCreateViewModel.changeMansion(poiItem)
                        }
                    }
                }
            }
        }

    // 区域选择
    private val mAreaDialog: AreaSelectDialog by lazy {
        AreaSelectDialog.Builder().apply {
            onAreaSelect = { province, city, distract ->
                mShopCreateViewModel.changeArea(
                    province.areaId,
                    province.areaName,
                    city.areaId,
                    city.areaName,
                    distract.areaId,
                    distract.areaName
                )
            }
        }.build()
    }

    private val mShopCreateViewModel by lazy {
        getActivityViewModelProvider(this)[ShopCreateViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SoftHideKeyBoardUtil.assistActivity(this)
    }

    override fun layoutId(): Int = R.layout.activity_shop_create

    override fun getVM(): ShopCreateViewModel = mShopCreateViewModel

    override fun backBtn(): View = mBinding.shopTitleBar.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 门店类型
        mBinding.mtivShopCreateType.onSelectedEvent = {
            mShopTypeDialog?.show(supportFragmentManager)
        }
        // 学校
        mBinding.mtivShopCreateSchoolName.onSelectedEvent = {
            startSearchSelect.launch(
                Intent(
                    this@ShopCreateActivity,
                    SearchSelectActivity::class.java
                ).apply {
                    putExtra(SEARCH_TYPE, SCHOOL)
                })
        }
        // 地区
        mBinding.mtivShopCreateArea.onSelectedEvent = {
            mAreaDialog.show(supportFragmentManager)
        }
        // 小区/大厦
        mBinding.mtivShopCreateMansion.onSelectedEvent = {
            if (null == mShopCreateViewModel.shopDetails.value?.cityId
                || -1 == mShopCreateViewModel.shopDetails.value?.cityId
            ) {
                SToast.showToast(this@ShopCreateActivity, "请先选择所在区域")
            } else {
                startSearchSelect.launch(
                    Intent(
                        this@ShopCreateActivity,
                        LocationSelectActivity::class.java
                    ).apply {
                        putExtra(
                            LocationSelectActivity.CityCode,
                            mShopCreateViewModel.shopDetails.value?.cityId?.toString()
                        )
                    }
                )
            }
        }

        // 营业时间
        mBinding.mtivShopCreateBusinessHours.onSelectedEvent = {
            val dailog = DateSelectorDialog.Builder().apply {
                selectModel = 1
                showModel = 4
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        Timber.i("选择的日期${DateTimeUtils.formatDateTime("yyyy-MM", date1)}")
                        //更换时间
                        mShopCreateViewModel.changeWorkTime(
                            "${
                                DateTimeUtils.formatDateTime(
                                    "HH:mm",
                                    date1
                                )
                            }-${DateTimeUtils.formatDateTime("HH:mm", date2)}"
                        )
                    }
                }
            }.build()
            dailog.show(supportFragmentManager)
        }

        // 业务类型
        mBinding.mtivShopCreateBusinessType.onSelectedEvent = {
            mShopCreateViewModel.shopBusinessTypeList.value?.let { list ->
                val select = mShopCreateViewModel.businessTypeValue.value?.split("、")
                select?.let {
                    list.forEach { type ->
                        type.isCheck = select.contains(type.businessName)
                    }
                }

                val multiDialog =
                    MultiSelectBottomSheetDialog.Builder("选择业务类型", list).apply {
                        onValueSureListener = object :
                            MultiSelectBottomSheetDialog.OnValueSureListener<ShopBusinessTypeEntity> {
                            override fun onValue(datas: List<ShopBusinessTypeEntity>) {
                                mShopCreateViewModel.changeBusinessType(datas)
                            }
                        }
                    }.build()
                multiDialog.show(supportFragmentManager)
            }
        }
    }

    override fun initEvent() {
        super.initEvent()

        // 初始化门店类型弹窗
        mShopCreateViewModel.shopTypeList.observe(this) { list ->
            if (null == mShopTypeDialog) {
                mShopTypeDialog =
                    CommonBottomSheetDialog.Builder("门店类型", list).apply {
                        onValueSureListener =
                            object : CommonBottomSheetDialog.OnValueSureListener<ShopTypeEntity> {
                                override fun onValue(data: ShopTypeEntity) {
                                    mShopCreateViewModel.changeShopType(data)
                                }
                            }
                    }.build()
            }
        }

        mShopCreateViewModel.jump.observe(this) {
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun initData() {
        mBinding.vm = mShopCreateViewModel

        mShopCreateViewModel.requestData()
    }
}