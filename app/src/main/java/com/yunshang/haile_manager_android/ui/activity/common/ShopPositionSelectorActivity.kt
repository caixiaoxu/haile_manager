package com.yunshang.haile_manager_android.ui.activity.common

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.ShopPositionSelectorViewModel
import com.yunshang.haile_manager_android.data.entities.ShopAndPositionSelectEntity
import com.yunshang.haile_manager_android.databinding.ActivityShopPositionSelectorBinding
import com.yunshang.haile_manager_android.databinding.ItemShopPositionSelectorBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.refresh.CustomDividerItemDecoration

class ShopPositionSelectorActivity :
    BaseBusinessActivity<ActivityShopPositionSelectorBinding, ShopPositionSelectorViewModel>(
        ShopPositionSelectorViewModel::class.java, BR.vm
    ) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemShopPositionSelectorBinding, ShopAndPositionSelectEntity>(
            R.layout.item_shop_manager_position, BR.item
        ) { mItemBinding, _, item ->

        }
    }

    override fun layoutId(): Int = R.layout.activity_shop_position_selector

    override fun backBtn(): View = mBinding.barShopPositionSelectTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()

        mViewModel.shopPositionList.observe(this) {
            mAdapter.refreshList(it, true)
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.rvShopPositionSelectList.layoutManager = LinearLayoutManager(this)
        ContextCompat.getDrawable(this, R.drawable.divder_efefef)?.let {
            mBinding.rvShopPositionSelectList.addItemDecoration(
                CustomDividerItemDecoration(
                    this,
                    CustomDividerItemDecoration.VERTICAL,
                    DimensionUtils.dip2px(this, 16f)
                )
            )
        }

    }

    override fun initData() {
        mViewModel.requestShopPositionList()
    }
}