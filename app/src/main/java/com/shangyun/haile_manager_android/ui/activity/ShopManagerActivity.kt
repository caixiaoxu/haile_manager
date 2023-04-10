package com.shangyun.haile_manager_android.ui.activity

import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.ShopManagerViewModel
import com.shangyun.haile_manager_android.databinding.ActivityShopManagerBinding

class ShopManagerActivity : BaseBusinessActivity<ActivityShopManagerBinding,ShopManagerViewModel>() {
    private val mShopManagerViewModel by lazy {
        getActivityViewModelProvider(this)[ShopManagerViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_shop_manager

    override fun getVM(): ShopManagerViewModel=mShopManagerViewModel
    override fun initView() {
        TODO("Not yet implemented")
    }

    override fun initData() {
        TODO("Not yet implemented")
    }


}