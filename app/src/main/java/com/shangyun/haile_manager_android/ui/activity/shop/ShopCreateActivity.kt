package com.shangyun.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.SearchSelectViewModel.Companion.SCHOOL
import com.shangyun.haile_manager_android.business.vm.SearchSelectViewModel.Companion.SEARCH_TYPE
import com.shangyun.haile_manager_android.business.vm.ShopCreateViewModel
import com.shangyun.haile_manager_android.data.entities.SchoolSelectEntity
import com.shangyun.haile_manager_android.data.entities.ShopTypeEntity
import com.shangyun.haile_manager_android.databinding.ActivityShopCreateBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.view.dialog.AreaSelectDialog
import com.shangyun.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog

class ShopCreateActivity :
    BaseBusinessActivity<ActivityShopCreateBinding, ShopCreateViewModel>() {

    companion object {
        const val SchoolResultCode = 10001
        const val SchoolResultData = "SchoolResultData"
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
        mBinding.mtivShopCreateMansion.onSelectedEvent={

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

                                    if (data.type != mShopCreateViewModel.shopTypeValue.value?.type) {
                                        mShopCreateViewModel.shopTypeValue.postValue(data)
                                        mShopCreateViewModel.shopDetails.value?.let {
                                            it.shopType = data.type
                                        }
                                        mShopCreateViewModel.changeSchool(null)
                                    }
                                }
                            }
                    }.build()
            }
        }
    }

    override fun initData() {
        mBinding.vm = mShopCreateViewModel

        mShopCreateViewModel.requestData()
    }
}