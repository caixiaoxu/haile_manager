package com.shangyun.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.services.core.PoiItem
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.ShopManagerViewModel
import com.shangyun.haile_manager_android.data.entities.SchoolSelectEntity
import com.shangyun.haile_manager_android.data.entities.ShopEntity
import com.shangyun.haile_manager_android.databinding.ActivityShopManagerBinding
import com.shangyun.haile_manager_android.databinding.ItemShopListBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.shangyun.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.shangyun.haile_manager_android.utils.NumberUtils

class ShopManagerActivity :
    BaseBusinessActivity<ActivityShopManagerBinding, ShopManagerViewModel>() {
    private val mShopManagerViewModel by lazy {
        getActivityViewModelProvider(this)[ShopManagerViewModel::class.java]
    }

    // 跳转
    private val startNext =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK){
                mBinding.rvShopList.requestRefresh()
            }
        }

    override fun layoutId(): Int = R.layout.activity_shop_manager

    override fun getVM(): ShopManagerViewModel = mShopManagerViewModel

    override fun backBtn(): View = mBinding.shopTitleBar.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE
        initRightBtn()

        mBinding.rvShopList.layoutManager = LinearLayoutManager(this)
        mBinding.rvShopList.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            ).apply {
                ResourcesCompat.getDrawable(resources, R.drawable.shape_height_8, null)?.let {
                    setDrawable(it)
                }
            })
        mBinding.rvShopList.adapter = CommonRecyclerAdapter<ItemShopListBinding, ShopEntity>(
            R.layout.item_shop_list,
            BR.item
        ) { mBinding, _, item ->
            mBinding?.item = item

            var title = StringUtils.getString(R.string.total_income)
            var value =
                NumberUtils.keepTwoDecimals(item.income) + StringUtils.getString(R.string.unit_yuan)
            var start = title.length + 1
            var end = title.length + 1 + value.length

            // 格式化总收益样式
            mBinding?.tvItemShopTotalIncome?.text =
                com.shangyun.haile_manager_android.utils.StringUtils.formatMultiStyleStr(
                    "$title：$value",
                    arrayOf(
                        ForegroundColorSpan(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.colorPrimary,
                                null
                            )
                        ),
                        AbsoluteSizeSpan(DimensionUtils.sp2px(this@ShopManagerActivity, 18f)),
                        StyleSpan(Typeface.BOLD),
                        TypefaceSpan("money")
                    ), start, end
                )
            title = StringUtils.getString(R.string.device_num)
            value = item.deviceNum.toString() + StringUtils.getString(R.string.unit_tai)
            start = title.length + 1
            end = title.length + 1 + value.length
            // 格式化设备数样式
            mBinding?.tvItemShopDeviceNum?.text =
                com.shangyun.haile_manager_android.utils.StringUtils.formatMultiStyleStr(
                    "$title：$value",
                    arrayOf(
                        AbsoluteSizeSpan(DimensionUtils.sp2px(this@ShopManagerActivity, 16f)),
                        StyleSpan(Typeface.BOLD),
                    ), start, end
                )
        }
        mBinding.rvShopList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<ShopEntity>() {
                override fun requestData(
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<ShopEntity>) -> Unit
                ) {
                    mShopManagerViewModel.requestShopList(page, pageSize, callBack)
                }
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
                        startNext.launch(
                            Intent(
                                this@ShopManagerActivity,
                                ShopCreateActivity::class.java
                            )
                        )
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
        mBinding.vm = mShopManagerViewModel
    }
}