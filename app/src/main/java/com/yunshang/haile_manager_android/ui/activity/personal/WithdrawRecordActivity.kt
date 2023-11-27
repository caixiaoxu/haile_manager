package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.activity.BaseBindingActivity
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.WithdrawRecordEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.databinding.ActivityWithdrawRecordBinding
import com.yunshang.haile_manager_android.databinding.ItemWithdrawRecordBinding
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.GlideUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WithdrawRecordActivity : BaseBindingActivity<ActivityWithdrawRecordBinding>() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)

    override fun layoutId(): Int = R.layout.activity_withdraw_record

    override fun backBtn(): View = mBinding.barWithdrawRecordTitle.getBackBtn()

    private val adapter by lazy {
        CommonRecyclerAdapter<ItemWithdrawRecordBinding, WithdrawRecordEntity>(
            R.layout.item_withdraw_record, BR.item
        ) { itemBinding, _, item ->

            itemBinding?.ivWithdrawRecordMain?.let {imageView->
                GlideUtils.loadImage(
                    imageView,
                    item.icon,
                    default = if (item.cashOutType == 1) R.mipmap.icon_withdraw_record_detail_alipay_main else R.mipmap.icon_bank_main_default
                )
            }
            itemBinding?.root?.setOnClickListener {
                startActivity(
                    Intent(
                        this@WithdrawRecordActivity,
                        WithdrawDetailActivity::class.java
                    ).apply {
                        putExtras(IntentParams.CommonParams.pack(item.id))
                    })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.WHITE
        mBinding.barWithdrawRecordTitle.getRightBtn().run {
            setText(R.string.export)
            textSize = 14f
            setTextColor(
                ContextCompat.getColor(
                    this@WithdrawRecordActivity,
                    R.color.common_txt_color
                )
            )
            setOnClickListener {
                startActivity(
                    Intent(
                        this@WithdrawRecordActivity,
                        WithdrawRecordExportActivity::class.java
                    )
                )
            }
        }

        mBinding.rvWithdrawRecordList.layoutManager = LinearLayoutManager(this)
        mBinding.rvWithdrawRecordList.adapter = adapter
        mBinding.rvWithdrawRecordList.requestData = object :
            CommonRefreshRecyclerView.OnRequestDataListener<WithdrawRecordEntity>() {
            override fun requestData(
                isRefresh: Boolean,
                page: Int,
                pageSize: Int,
                callBack: (responseList: ResponseList<out WithdrawRecordEntity>?) -> Unit
            ) {
                launch({
                    ApiRepository.dealApiResult(
                        mCapitalRepo.requestWithdrawRecordList(
                            page,
                            pageSize
                        )
                    )?.let {
                        withContext(Dispatchers.Main) {
                            callBack.invoke(it)
                        }
                    }
                })
            }
        }

        mBinding.rvWithdrawRecordList.requestRefresh()
    }
}