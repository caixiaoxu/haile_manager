package com.yunshang.haile_manager_android.ui.activity.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.ShopBusinessViewModel
import com.yunshang.haile_manager_android.databinding.ActivityShopBusinessBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class ShopBusinessActivity :
    BaseBusinessActivity<ActivityShopBusinessBinding, ShopBusinessViewModel>(ShopBusinessViewModel::class.java) {

    override fun layoutId(): Int  = R.layout.activity_shop_business

    override fun initView() {
    }

    override fun initData() {
    }
}