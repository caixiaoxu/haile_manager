package com.yunshang.haile_manager_android.ui.activity.coupon

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.CouponManageViewModel
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.CouponEntity
import com.yunshang.haile_manager_android.databinding.ActivityCouponManageBinding
import com.yunshang.haile_manager_android.databinding.ItemCouponListBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceListBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView

class CouponManageActivity :
    BaseBusinessActivity<ActivityCouponManageBinding, CouponManageViewModel>(
        CouponManageViewModel::class.java, BR.vm
    ) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemCouponListBinding, CouponEntity>(
            R.layout.item_coupon_list,
            BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.root?.setOnClickListener {

            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_coupon_manage

    override fun backBtn(): View = mBinding.barCouponManageTitle.getBackBtn()

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        mBinding.barCouponManageTitle.getRightBtn(true).run {
            setText(R.string.issue_coupons)
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.mipmap.icon_add, 0, 0, 0
            )
            compoundDrawablePadding = DimensionUtils.dip2px(this@CouponManageActivity, 4f)
            setOnClickListener {
                startActivity(Intent(this@CouponManageActivity, IssueCouponsActivity::class.java))
            }
        }
    }

    override fun initEvent() {
        super.initEvent()

        LiveDataBus.with(BusEvents.COUPON_LIST_STATUS)?.observe(this) {
            mViewModel.requestData(1)
            mBinding.rvCouponList.requestRefresh()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        initRightBtn()

        // 设备状态
        mBinding.indicatorCouponStatus.navigator = CommonNavigator(this).apply {

            adapter = object : CommonNavigatorAdapter() {
                override fun getCount(): Int = mViewModel.couponStatus.size

                override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                    return SimplePagerTitleView(context).apply {
                        normalColor = Color.parseColor("#666666")
                        selectedColor = Color.WHITE
                        mViewModel.couponStatus[index].run {
                            num.observe(this@CouponManageActivity) { n ->
                                text = title + if (0 < n) " $n" else " 0"
                            }
                            setOnClickListener {
                                mViewModel.curCouponStatus.value = value
                                onPageSelected(index)
                                notifyDataSetChanged()
                            }
                        }
                    }
                }

                override fun getIndicator(context: Context?): IPagerIndicator {
                    return WrapPagerIndicator(context).apply {
                        verticalPadding =
                            DimensionUtils.dip2px(this@CouponManageActivity, 4f)
                        fillColor = ContextCompat.getColor(
                            this@CouponManageActivity,
                            R.color.colorPrimary
                        )
                        roundRadius =
                            DimensionUtils.dip2px(this@CouponManageActivity, 14f).toFloat()
                    }
                }
            }
        }

        // 券列表
        mBinding.tvCouponType.setOnClickListener {
            CommonBottomSheetDialog.Builder(
                getString(R.string.coupon_type),
                mViewModel.couponTypeList
            ).apply {
                mustSelect = true
                onValueSureListener =
                    object :
                        CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                        override fun onValue(data: SearchSelectParam?) {
                            mViewModel.selectCouponType.value =
                                if (-1 == data?.id) null else data?.id
                        }
                    }
            }.build().show(supportFragmentManager)
        }

        // 刷新
        mBinding.tvCouponListRefresh.setOnClickListener {
            mBinding.rvCouponList.requestRefresh()
        }

        // 券列表
        mBinding.rvCouponList.layoutManager = LinearLayoutManager(this)
        mBinding.rvCouponList.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            ).apply {
                ResourcesCompat.getDrawable(resources, R.drawable.divide_size8, null)?.let {
                    setDrawable(it)
                }
            })
        mBinding.rvCouponList.adapter = mAdapter
        mBinding.rvCouponList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<CouponEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out CouponEntity>?) -> Unit
                ) {
                    mViewModel.requestCouponList(page, pageSize, callBack)
                }
            }
    }

    override fun initData() {
        mViewModel.requestData()
        mBinding.rvCouponList.requestRefresh(true)
    }
}