package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.SearchSelectViewModel.Companion.SCHOOL
import com.yunshang.haile_manager_android.business.vm.SearchSelectViewModel.Companion.SEARCH_TYPE
import com.yunshang.haile_manager_android.business.vm.ShopCreateAndUpdateViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.SchoolSelectEntity
import com.yunshang.haile_manager_android.data.entities.ShopTypeEntity
import com.yunshang.haile_manager_android.databinding.ActivityShopCreateAndUpdateBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectActivity
import com.yunshang.haile_manager_android.ui.view.dialog.AreaSelectDialog
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog

class ShopCreateAndUpdateActivity :
    BaseBusinessActivity<ActivityShopCreateAndUpdateBinding, ShopCreateAndUpdateViewModel>(
        ShopCreateAndUpdateViewModel::class.java,
        BR.vm
    ) {

    companion object {
        const val SchoolResultCode = 10001
        const val SchoolResultData = "SchoolResultData"

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
                            mViewModel.createAndUpdateEntity.value?.changeSchool(school)
                        }
                    }
                }
                IntentParams.LocationParams.LocationResultCode -> {
                    it.data?.let { intent ->
                        IntentParams.LocationParams.parseLocationResultData(intent)
                            ?.let { poiItem ->
                                mViewModel.createAndUpdateEntity.value?.changeMansion(
                                    poiItem.title,
                                    poiItem.latitude,
                                    poiItem.longitude,
                                    poiItem.address
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
                mViewModel.createAndUpdateEntity.value?.changeArea(
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
            if (null == mViewModel.createAndUpdateEntity.value?.shopTypeVal) {
                SToast.showToast(this@ShopCreateAndUpdateActivity, "请先选择门店类型")
            } else {
                mAreaDialog.show(supportFragmentManager)
            }
        }
        // 小区/大厦
        mBinding.mtivShopCreateMansion.onSelectedEvent = {
            if (null == mViewModel.createAndUpdateEntity.value?.shopTypeVal) {
                SToast.showToast(this@ShopCreateAndUpdateActivity, "请先选择门店类型")
            } else if (null == mViewModel.createAndUpdateEntity.value?.cityId
                || -1 == mViewModel.createAndUpdateEntity.value?.cityId
            ) {
                SToast.showToast(this@ShopCreateAndUpdateActivity, "请先选择所在区域")
            } else {
                startSearchSelect.launch(
                    Intent(
                        this@ShopCreateAndUpdateActivity,
                        CurLocationSelectorActivity::class.java
                    ).apply {
                        putExtras(
                            IntentParams.LocationSelectParams.packCity(mViewModel.createAndUpdateEntity.value?.cityName)
                        )
                    }
                )
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
                                    mViewModel.createAndUpdateEntity.value?.changeShopType(data)
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