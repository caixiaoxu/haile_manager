package com.shangyun.haile_manager_android.ui.activity.shop

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.DimensionUtils
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.ShopManagerViewModel
import com.shangyun.haile_manager_android.data.entities.ShopEntity
import com.shangyun.haile_manager_android.databinding.ActivityShopManagerBinding
import com.shangyun.haile_manager_android.databinding.ItemShopListBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.adapter.CommonRecyclerAdapter
import timber.log.Timber

class ShopManagerActivity :
    BaseBusinessActivity<ActivityShopManagerBinding, ShopManagerViewModel>() {
    private val mShopManagerViewModel by lazy {
        getActivityViewModelProvider(this)[ShopManagerViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_shop_manager

    override fun getVM(): ShopManagerViewModel = mShopManagerViewModel

    override fun backBtn(): View = mBinding.shopTitleBar.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE
        initRightBtn()

        mBinding.rrvShopList.layoutManager = LinearLayoutManager(this)
        mBinding.rrvShopList.adapter = CommonRecyclerAdapter<ItemShopListBinding, ShopEntity>(
            R.layout.item_shop_list,
            BR.item
        ) { mBinding, item ->

        }
        mBinding.rrvShopList.requestData = { page, pageSize, callBack ->
            mShopManagerViewModel.requestShopList(page, pageSize, callBack)
        }
    }

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        mBinding.shopTitleBar.getRightArea()
            .addView(
                Button(this).apply {
                    setText(R.string.add_shop)
                    setTextColor(Color.WHITE)
                    textSize = 14f
                    setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.mipmap.icon_add, 0, 0, 0
                    )
                    compoundDrawablePadding = DimensionUtils.dip2px(this@ShopManagerActivity, 4f)
                    val pH = DimensionUtils.dip2px(this@ShopManagerActivity, 12f)
                    val pV = DimensionUtils.dip2px(this@ShopManagerActivity, 4f)
                    setPadding(pH, pV, pH, pV)
                    gravity = Gravity.CENTER
                    setBackgroundResource(R.drawable.shape_sf0a258_r22)
                    setOnClickListener {
                        // TODO 添加店铺
                    }
                },
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    DimensionUtils.dip2px(this@ShopManagerActivity, 28f)
                ).apply {
                    setMargins(0, 0, DimensionUtils.dip2px(this@ShopManagerActivity, 16f), 0)
                }
            )
    }

    override fun initData() {
    }

}