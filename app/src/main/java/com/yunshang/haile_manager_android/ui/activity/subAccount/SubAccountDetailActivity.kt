package com.yunshang.haile_manager_android.ui.activity.subAccount

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.SubAccountDetailViewModel
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.Category
import com.yunshang.haile_manager_android.data.entities.SubAccountShop
import com.yunshang.haile_manager_android.databinding.ActivitySubAccountDetailBinding
import com.yunshang.haile_manager_android.databinding.ItemSubAccountDetailShopBinding
import com.yunshang.haile_manager_android.databinding.ItemSubAccountDetailShopCategoryBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.utils.DateTimeUtils

class SubAccountDetailActivity :
    BaseBusinessActivity<ActivitySubAccountDetailBinding, SubAccountDetailViewModel>(SubAccountDetailViewModel::class.java,BR.vm) {

    companion object {
        const val UserId = "userId"
    }

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemSubAccountDetailShopBinding, SubAccountShop>(
            R.layout.item_sub_account_detail_shop, BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.llSubAccountDetailShopCategory?.let { linear ->
                if (linear.childCount > 0) {
                    linear.removeAllViews()
                }

                val inflater = LayoutInflater.from(this@SubAccountDetailActivity)
                item.categories.forEach { category ->
                    DataBindingUtil.inflate<ItemSubAccountDetailShopCategoryBinding>(
                        inflater,
                        R.layout.item_sub_account_detail_shop_category,
                        null,
                        false
                    ).let {
                        it.v1 = category.name
                        it.v2 = "${category.ratio}%"
                        it.v3 =
                            DateTimeUtils.formatDateTimeForStr(category.effectiveDate, "yyyy/MM/dd")
                        it.ibSubAccountDetailShopCategoryV4.setOnClickListener {
                            showOperateDialog(intArrayOf(item.id), category)
                        }
                        linear.addView(
                            it.root,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }
                }
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_sub_account_detail

    override fun backBtn(): View = mBinding.barSubAccountDetailTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.userId = intent.getIntExtra(UserId, -1)
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.jump.observe(this) {
            finish()
        }

        LiveDataBus.with(BusEvents.SUB_ACCOUNT_DETAIL_STATUS)?.observe(this) {
            mViewModel.requestData()
        }

        mViewModel.subAccountDetail.observe(this) {
            it?.shops?.let { list ->
                mAdapter.refreshList(list.toMutableList(), true)
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.rvSubAccountShopList.layoutManager = LinearLayoutManager(this)
        ResourcesCompat.getDrawable(
            resources,
            R.drawable.divide_size8,
            null
        )?.let { drawable ->
            mBinding.rvSubAccountShopList.addItemDecoration(
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
                    setDrawable(drawable)
                })
        }
        mBinding.rvSubAccountShopList.adapter = mAdapter
    }

    private fun showOperateDialog(shopIds: IntArray, category: Category) {
        CommonBottomSheetDialog.Builder(
            "", arrayListOf(
                SearchSelectParam(1, StringUtils.getString(R.string.edit)),
                SearchSelectParam(2, StringUtils.getString(R.string.delete)),
            )
        ).apply {
            onValueSureListener = object :
                CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                override fun onValue(data: SearchSelectParam) {
                    if (1 == data.id) {
                        mViewModel.subAccountDetail.value?.let { detail ->
                            startActivity(
                                Intent(
                                    this@SubAccountDetailActivity,
                                    SubAccountCreateActivity::class.java
                                ).apply {
                                    putExtra(SubAccountCreateActivity.UserId, detail.id)
                                    putExtra(
                                        SubAccountCreateActivity.CategoryIds,
                                        intArrayOf(category.id)
                                    )
                                    putExtra(SubAccountCreateActivity.ShopIds, shopIds)
                                    putExtra(
                                        SubAccountCreateActivity.Ratio,
                                        category.ratio.toString()
                                    )
                                    putExtra(
                                        SubAccountCreateActivity.EffectiveDate,
                                        category.effectiveDate
                                    )
                                }
                            )
                        }
                    } else {
                        mViewModel.deleteSubAccount(this@SubAccountDetailActivity,category.settingId)
                    }
                }
            }
        }.build()
            .show(supportFragmentManager)
    }

    override fun initData() {
        mViewModel.requestData()
    }
}