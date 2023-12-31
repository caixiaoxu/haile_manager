package com.yunshang.haile_manager_android.ui.activity.common

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.ShopPositionSelectorViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.ShopAndPositionSelectEntity
import com.yunshang.haile_manager_android.data.entities.ShopPositionSelect
import com.yunshang.haile_manager_android.databinding.ActivityShopPositionSelectorBinding
import com.yunshang.haile_manager_android.databinding.ItemShopPositionSelectorBinding
import com.yunshang.haile_manager_android.databinding.ItemShopPositionSelectorPositionBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.shop.ShopPositionCreateActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.visibility
import com.yunshang.haile_manager_android.ui.view.refresh.CustomDividerItemDecoration

class ShopPositionSelectorActivity :
    BaseBusinessActivity<ActivityShopPositionSelectorBinding, ShopPositionSelectorViewModel>(
        ShopPositionSelectorViewModel::class.java, BR.vm
    ) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemShopPositionSelectorBinding, ShopAndPositionSelectEntity>(
            R.layout.item_shop_position_selector, BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.showPosition = mViewModel.showPosition
            mItemBinding?.canMultiSelect = mViewModel.canMultiSelect
            mItemBinding?.cbShopPositionSelectorShopAll?.setOnClickListener {
                item.selectAllOrNo()
                mViewModel.checkIsAll()
            }

            mItemBinding?.rvShopPositionSelectorPosition?.layoutManager = LinearLayoutManager(this)
            mItemBinding?.rvShopPositionSelectorPosition?.adapter =
                CommonRecyclerAdapter<ItemShopPositionSelectorPositionBinding, ShopPositionSelect>(
                    R.layout.item_shop_position_selector_position, BR.item
                ) { mPositionItemBinding, pos, posistionItem ->
                    mPositionItemBinding?.lineShopPositionSelectorPositionBottom?.visibility(pos != item.positionList!!.size - 1)
                    mPositionItemBinding?.root?.setOnClickListener {
                        if (!mViewModel.canMultiSelect) {
                            mViewModel.shopPositionList.value?.forEach { position ->
                                position.selectType = 0
                                position.positionList?.forEach { select ->
                                    select.selectVal = false
                                }
                            }
                        }
                        posistionItem.selectVal = !posistionItem.selectVal
                        item.checkIsAll()
                        if (mViewModel.canMultiSelect) {
                            mViewModel.checkIsAll()
                        }
                    }
                }.also { positionAdapter ->
                    item.positionList?.let {
                        positionAdapter.refreshList(it, true)
                    }
                }

            mItemBinding?.tvShopPositionSelectorPositionAdd?.setOnClickListener {
                // 跳转新增点位
                startActivity(
                    Intent(
                        this@ShopPositionSelectorActivity,
                        ShopPositionCreateActivity::class.java
                    ).apply {
                        if (null != item.id) {
                            putExtras(IntentParams.ShopParams.pack(item.id, item.name))
                        }
                    }
                )
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_shop_position_selector

    override fun backBtn(): View = mBinding.barShopPositionSelectTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()

        mViewModel.canMultiSelect =
            IntentParams.ShopPositionSelectorParams.parseCanMultiSelect(intent)
        mViewModel.showPosition = IntentParams.ShopPositionSelectorParams.parseShowPosition(intent)
        mViewModel.canSelectAll = IntentParams.ShopPositionSelectorParams.parseCanSelectAll(intent)
        mViewModel.oldShopPositionList =
            IntentParams.ShopPositionSelectorParams.parseSelectList(intent)

        mViewModel.shopIdList = IntentParams.ShopPositionSelectorParams.parseShopIdList(intent)
    }

    override fun initEvent() {
        super.initEvent()

        LiveDataBus.with(BusEvents.PT_LIST_STATUS)?.observe(this) {
            mViewModel.requestShopPositionList()
        }

        mViewModel.shopPositionList.observe(this) {
            mAdapter.refreshList(it, true)
            mViewModel.checkIsAll()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        IntentParams.ShopPositionSelectorParams.parseShopTitle(
            intent
        )?.let { title ->
            mBinding.barShopPositionSelectTitle.setTitle(title)
        } ?: mBinding.barShopPositionSelectTitle.setTitle(R.string.coupon_shop)

        mBinding.btnShopPositionSelectAll.setOnClickListener {
            mViewModel.shopPositionList.value?.let { list ->
                val isSelect = !mViewModel.isAll.value!!
                list.forEach { select ->
                    select.positionList?.forEach { item ->
                        item.selectVal = isSelect
                    }
                    select.selectType = if (isSelect) 2 else 0
                }
                mViewModel.isAll.value = isSelect
            }
        }

        mBinding.etShopPositionSelectSearchContent.onTextChange = {
            mViewModel.searchShopPositionList(
                mBinding.etShopPositionSelectSearchContent.text.toString().trim()
            )
        }

        mBinding.rvShopPositionSelectList.layoutManager = LinearLayoutManager(this)
        ContextCompat.getDrawable(this, R.drawable.divder_efefef)?.let {
            mBinding.rvShopPositionSelectList.addItemDecoration(
                CustomDividerItemDecoration(
                    this,
                    CustomDividerItemDecoration.VERTICAL,
                    DimensionUtils.dip2px(this, 16f)
                ).apply {
                    setDrawable(it)
                }
            )
        }
        mBinding.rvShopPositionSelectList.adapter = mAdapter

        mBinding.btnShopPositionSelectSubmit.setOnClickListener {
            val list = mViewModel.shopPositionList.value?.filter { item -> item.selectType != 0 }
                ?.toMutableList()
            list?.forEach {
                it.positionList =
                    it.positionList?.filter { item -> item.selectVal }?.toMutableList()
            }
            if (IntentParams.ShopPositionSelectorParams.parseMustSelect(intent)) {
                if (list.isNullOrEmpty()) {
                    SToast.showToast(this, "请先选择适用门店")
                    return@setOnClickListener
                }
                if (list.any { item -> item.positionList.isNullOrEmpty() }) {
                    SToast.showToast(this, "所选的门店中，有门店无点位，请先添加点位")
                    return@setOnClickListener
                }
            }
            setResult(
                IntentParams.ShopPositionSelectorParams.ShopPositionSelectorResultCode,
                Intent().apply {
                    putExtras(IntentParams.ShopPositionSelectorParams.packResult(list))
                })
            finish()
        }
    }

    override fun initData() {
        mViewModel.requestShopPositionList()
    }
}