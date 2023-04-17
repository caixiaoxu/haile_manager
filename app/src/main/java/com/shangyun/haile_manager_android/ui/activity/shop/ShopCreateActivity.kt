package com.shangyun.haile_manager_android.ui.activity.shop

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.ShopCreateViewModel
import com.shangyun.haile_manager_android.data.entities.ShopTypeEntity
import com.shangyun.haile_manager_android.databinding.ActivityShopCreateBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog

class ShopCreateActivity :
    BaseBusinessActivity<ActivityShopCreateBinding, ShopCreateViewModel>() {

    private val startSchoolSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.data != null && it.resultCode == Activity.RESULT_OK) {
            } else {
            }
        }

    private val mShopCreateViewModel by lazy {
        getActivityViewModelProvider(this)[ShopCreateViewModel::class.java]
    }

    private var mShopTypeDialog: CommonBottomSheetDialog<ShopTypeEntity>? = null

    override fun layoutId(): Int = R.layout.activity_shop_create

    override fun getVM(): ShopCreateViewModel = mShopCreateViewModel

    override fun backBtn(): View = mBinding.shopTitleBar.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.mtivShopCreateType.onSelectedEvent = {
            mShopTypeDialog?.show(supportFragmentManager)
        }
        mBinding.mtivShopCreateSchoolName.onSelectedEvent = {
            startSchoolSelect.launch(Intent(this@ShopCreateActivity, SchoolSelectActivity::class.java))
        }
    }

    override fun initEvent() {
        super.initEvent()

        mShopCreateViewModel.shopTypeList.observe(this) { list ->
            if (null == mShopTypeDialog) {
                mShopTypeDialog =
                    CommonBottomSheetDialog.Builder("门店类型", list).apply {
                        onValueSureListener =
                            object : CommonBottomSheetDialog.OnValueSureListener<ShopTypeEntity> {
                                override fun onValue(data: ShopTypeEntity) {
                                    mShopCreateViewModel.shopTypeValue.postValue(data)
                                    mShopCreateViewModel.shopDetails.value?.let {
                                        it.shopType = data.type
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