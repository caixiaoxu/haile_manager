package com.yunshang.haile_manager_android.ui.activity.recharge

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.HaiXinRechargeAccountsViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.IntentParams.SearchSelectTypeParam
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.SearchType
import com.yunshang.haile_manager_android.data.entities.HaixinRechargeAccountEntity
import com.yunshang.haile_manager_android.databinding.ActivityHaixinRechargeAccountsBinding
import com.yunshang.haile_manager_android.databinding.ItemHaixinRechargeAccountsBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.UserPermissionUtils

class HaiXinRechargeAccountsActivity :
    BaseBusinessActivity<ActivityHaixinRechargeAccountsBinding, HaiXinRechargeAccountsViewModel>(
        HaiXinRechargeAccountsViewModel::class.java, BR.vm
    ) {

    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                intent.getStringExtra(SearchSelectTypeParam.ResultData)?.let { json ->

                    GsonUtils.json2List(json, SearchSelectParam::class.java)?.let { selected ->
                        mViewModel.selectDepartment.value =
                            if (selected.isNotEmpty()) selected[0] else null
                    }
                }
            }
        }

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemHaixinRechargeAccountsBinding, HaixinRechargeAccountEntity>(
            R.layout.item_haixin_recharge_accounts,
            BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.tvRechargeAccountsRecycle?.visibility =
                if (UserPermissionUtils.hasVipRechargeRecyclePermission()) View.VISIBLE else View.GONE
            mItemBinding?.tvRechargeAccountsRecycle?.setOnClickListener {
                startActivity(
                    Intent(
                        this@HaiXinRechargeAccountsActivity,
                        HaiXinRechargeRecycleActivity::class.java
                    ).apply {
                        putExtras(
                            IntentParams.RechargeRecycleParams.pack(
                                item.userId,
                                item.phone,
                                item.shopId,
                                item.shopName,
                                item.principalAmount,
                                item.presentAmount
                            )
                        )
                    }
                )
            }
            mItemBinding?.root?.setOnClickListener {
                startActivity(
                    Intent(
                        this@HaiXinRechargeAccountsActivity,
                        HaiXinRechargeAccountDetailListActivity::class.java
                    ).apply {
                        putExtras(
                            IntentParams.RechargeAccountDetailParams.pack(
                                item.userId,
                                item.shopId,
                                item.shopName
                            )
                        )
                    }
                )
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_haixin_recharge_accounts

    override fun backBtn(): View = mBinding.barRechargeAccountsTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.searchKeyword.value = IntentParams.SearchParams.parseKeyWord(intent)
    }

    override fun initEvent() {
        super.initEvent()

        // 选择店铺
        mViewModel.selectDepartment.observe(this) {
            mBinding.tvRechargeAccountsDepartment.text = it?.name ?: ""
            mBinding.rvRechargeAccountsList.requestRefresh()
        }

        LiveDataBus.with(BusEvents.HAIXIN_USER_LIST_STATUS)?.observe(this) {
            mBinding.rvRechargeAccountsList.requestRefresh()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.viewRechargeAccountsSearchBg.setOnClickListener {
            startActivity(
                Intent(
                    this@HaiXinRechargeAccountsActivity,
                    SearchActivity::class.java
                ).apply {
                    putExtra(SearchType.SearchType, SearchType.HaiXinRechargeAccount)
                })
        }

        // 所属门店
        mBinding.tvRechargeAccountsDepartment.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@HaiXinRechargeAccountsActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(
                        SearchSelectTypeParam.pack(
                            SearchSelectTypeParam.SearchSelectTypeShop,
                            mustSelect = false
                        )
                    )
                }
            )
        }

        // 刷新
        mBinding.tvRechargeAccountsListRefresh.setOnClickListener {
            mBinding.rvRechargeAccountsList.requestRefresh()
        }

        mBinding.rvRechargeAccountsList.layoutManager = LinearLayoutManager(this)
        mBinding.rvRechargeAccountsList.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            ).apply {
                ResourcesCompat.getDrawable(resources, R.drawable.divide_size8, null)?.let {
                    setDrawable(it)
                }
            })
        mBinding.rvRechargeAccountsList.adapter = mAdapter
        mBinding.rvRechargeAccountsList.requestData =
            object :
                CommonRefreshRecyclerView.OnRequestDataListener<HaixinRechargeAccountEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out HaixinRechargeAccountEntity>?) -> Unit
                ) {
                    mViewModel.requestRechargeList(page, pageSize, callBack)
                }
            }
    }

    override fun initData() {
        mViewModel.requestData()
    }
}