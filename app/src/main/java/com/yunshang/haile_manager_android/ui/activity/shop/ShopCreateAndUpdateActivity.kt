package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.amap.api.services.core.PoiItem
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.SearchSelectViewModel.Companion.SCHOOL
import com.yunshang.haile_manager_android.business.vm.SearchSelectViewModel.Companion.SEARCH_TYPE
import com.yunshang.haile_manager_android.business.vm.ShopCreateAndUpdateViewModel
import com.yunshang.haile_manager_android.data.entities.SchoolSelectEntity
import com.yunshang.haile_manager_android.data.entities.ShopBusinessTypeEntity
import com.yunshang.haile_manager_android.data.entities.ShopTypeEntity
import com.yunshang.haile_manager_android.databinding.ActivityShopCreateAndUpdateBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectActivity
import com.yunshang.haile_manager_android.ui.view.dialog.AreaSelectDialog
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import timber.log.Timber
import java.util.*

class ShopCreateAndUpdateActivity :
    BaseBusinessActivity<ActivityShopCreateAndUpdateBinding, ShopCreateAndUpdateViewModel>(
        ShopCreateAndUpdateViewModel::class.java,
        BR.vm
    ) {

    companion object {
        const val SchoolResultCode = 10001
        const val SchoolResultData = "SchoolResultData"
        const val LocationResultCode = 10002
        const val LocationResultData = "LocationResultData"

        const val ShopDetail = "ShopDetail"
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
                            mViewModel.changeSchool(school)
                        }
                    }
                }
                LocationResultCode -> {
                    it.data?.getStringExtra(LocationResultData)?.let { json ->
                        GsonUtils.json2Class(json, PoiItem::class.java)?.let { poiItem ->
                            mViewModel.changeMansion(
                                poiItem.title,
                                poiItem.latLonPoint.latitude,
                                poiItem.latLonPoint.longitude,
                                poiItem.provinceName + poiItem.cityName + poiItem.adName + poiItem.snippet
                            )
                        }
                    }
                }
            }
        }

    // 区域选择
    private val mAreaDialog: AreaSelectDialog by lazy {
        AreaSelectDialog.Builder().apply {
            onAreaSelect = { province, city, distract ->
                mViewModel.changeArea(
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

    override fun layoutId(): Int = R.layout.activity_shop_create_and_update

    override fun backBtn(): View = mBinding.shopTitleBar.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.updateShopDetail(intent.getStringExtra(ShopDetail))
    }

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
                    this@ShopCreateAndUpdateActivity,
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
            if (null == mViewModel.createAndUpdateEntity.value?.cityId
                || -1 == mViewModel.createAndUpdateEntity.value?.cityId
            ) {
                SToast.showToast(this@ShopCreateAndUpdateActivity, "请先选择所在区域")
            } else {
                startSearchSelect.launch(
                    Intent(
                        this@ShopCreateAndUpdateActivity,
                        LocationSelectActivity::class.java
                    ).apply {
                        putExtra(
                            LocationSelectActivity.CityCode,
                            mViewModel.createAndUpdateEntity.value?.cityId?.toString()
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
                        Timber.i("选择的日期${DateTimeUtils.formatDateTime(date1, "yyyy-MM")}")
                        //更换时间
                        mViewModel.changeWorkTime(
                            "${
                                DateTimeUtils.formatDateTime(
                                    date1,
                                    "HH:mm"
                                )
                            }-${DateTimeUtils.formatDateTime(date2, "HH:mm")}"
                        )
                    }
                }
            }.build()
            dailog.show(supportFragmentManager)
        }

        // 业务类型
        mBinding.mtivShopCreateBusinessType.onSelectedEvent = {
            mViewModel.shopBusinessTypeList.value?.let { list ->
                val select = mViewModel.businessTypeValue.value?.split("、")
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
                                mViewModel.changeBusinessType(datas)
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
        mViewModel.shopTypeList.observe(this) { list ->
            if (null == mShopTypeDialog) {
                mShopTypeDialog =
                    CommonBottomSheetDialog.Builder("门店类型", list).apply {
                        onValueSureListener =
                            object : CommonBottomSheetDialog.OnValueSureListener<ShopTypeEntity> {
                                override fun onValue(data: ShopTypeEntity?) {
                                    mViewModel.changeShopType(data)
                                }
                            }
                    }.build()
            }
        }

        mViewModel.jump.observe(this) {
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun initData() {
        mViewModel.requestData()
    }
}