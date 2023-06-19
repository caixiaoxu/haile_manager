package com.yunshang.haile_manager_android.ui.activity.recharge

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.HaiXinRefundRecordViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.OrderListEntity
import com.yunshang.haile_manager_android.data.entities.RefundRecordEntity
import com.yunshang.haile_manager_android.databinding.ActivityHaixinRefundRecordBinding
import com.yunshang.haile_manager_android.databinding.ItemRefundRecordBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.UserPermissionUtils
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView

class HaiXinRefundRecordActivity :
    BaseBusinessActivity<ActivityHaixinRefundRecordBinding, HaiXinRefundRecordViewModel>(
        HaiXinRefundRecordViewModel::class.java, BR.vm
    ) {

    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)?.let { json ->

                    GsonUtils.json2List(json, SearchSelectParam::class.java)?.let { selected ->
                        if (selected.isNotEmpty()) {
                            when (it.resultCode) {
                                IntentParams.SearchSelectTypeParam.ShopResultCode -> {
                                    mViewModel.selectDepartment.value = selected[0]
                                }
                            }
                        }
                    }
                }
            }
        }

    private val mAdapter: CommonRecyclerAdapter<ItemRefundRecordBinding, RefundRecordEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_refund_record, BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.root?.setOnClickListener {
                if (!UserPermissionUtils.hasVipRefundDetailPermission()) return@setOnClickListener
                startActivity(
                    Intent(
                        this@HaiXinRefundRecordActivity,
                        HaiXinRefundRecordDetailActivity::class.java
                    ).apply {
                        putExtras(IntentParams.CommonParams.pack(item.id))
                    })
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_haixin_refund_record

    override fun backBtn(): View = mBinding.barRefundRecordTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()

        // 选择店铺
        mViewModel.selectDepartment.observe(this) {
            mBinding.tvRefundRecordDepartment.text = it.name
            mBinding.rvRefundRecordList.requestRefresh()
        }

        // 列表刷新
        LiveDataBus.with(BusEvents.HAIXIN_REFUND_RECORD_LIST_STATUS)
            ?.observe(this) {
                mBinding.rvRefundRecordList.requestRefresh()
            }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.etRefundRecordSearch.onTextChange = {
            mBinding.rvRefundRecordList.requestRefresh()
        }

        // 状态
        mBinding.indicatorRefundRecordStatus.navigator = CommonNavigator(this).apply {
            adapter = object : CommonNavigatorAdapter() {
                override fun getCount(): Int = mViewModel.refundStatus.size

                override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                    return SimplePagerTitleView(context).apply {
                        normalColor = Color.parseColor("#666666")
                        selectedColor = Color.WHITE
                        mViewModel.refundStatus[index].run {
                            text = title
                            setOnClickListener {
                                mViewModel.curRefundStatus.value = value
                                onPageSelected(index)
                                notifyDataSetChanged()
                            }
                        }
                    }
                }

                override fun getIndicator(context: Context?): IPagerIndicator {
                    return WrapPagerIndicator(context).apply {
                        verticalPadding = DimensionUtils.dip2px(this@HaiXinRefundRecordActivity, 4f)
                        fillColor = ContextCompat.getColor(
                            this@HaiXinRefundRecordActivity,
                            R.color.colorPrimary
                        )
                        roundRadius =
                            DimensionUtils.dip2px(this@HaiXinRefundRecordActivity, 14f).toFloat()
                    }
                }
            }
        }

        // 切换工作状态
        mViewModel.curRefundStatus.observe(this) {
            mBinding.rvRefundRecordList.requestRefresh()
        }

        // 所属门店
        mBinding.tvRefundRecordDepartment.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@HaiXinRefundRecordActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(IntentParams.SearchSelectTypeParam.pack(IntentParams.SearchSelectTypeParam.SearchSelectTypeShop))
                }
            )
        }

        // 刷新
        mBinding.tvRefundRecordListRefresh.setOnClickListener {
            mBinding.rvRefundRecordList.requestRefresh()
        }

        mBinding.rvRefundRecordList.layoutManager = LinearLayoutManager(this)
        mBinding.rvRefundRecordList.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            ).apply {
                ResourcesCompat.getDrawable(resources, R.drawable.divide_size8, null)?.let {
                    setDrawable(it)
                }
            })
        mBinding.rvRefundRecordList.adapter = mAdapter
        mBinding.rvRefundRecordList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<RefundRecordEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out RefundRecordEntity>?) -> Unit
                ) {
                    mViewModel.requestRefundRecordList(page, pageSize, callBack)
                }
            }

    }

    override fun initData() {
    }
}