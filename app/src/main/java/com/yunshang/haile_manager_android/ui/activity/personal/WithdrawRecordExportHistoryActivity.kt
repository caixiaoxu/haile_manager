package com.yunshang.haile_manager_android.ui.activity.personal

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.WithdrawRecordExportHistoryViewModel
import com.yunshang.haile_manager_android.data.entities.ExportHistoryEntity
import com.yunshang.haile_manager_android.databinding.ActivityWithdrawRecordExportHistoryBinding
import com.yunshang.haile_manager_android.databinding.ItemWithdrawRecordExportHistoryBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView

class WithdrawRecordExportHistoryActivity :
    BaseBusinessActivity<ActivityWithdrawRecordExportHistoryBinding, WithdrawRecordExportHistoryViewModel>(
        WithdrawRecordExportHistoryViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_withdraw_record_export_history

    override fun backBtn(): View = mBinding.barWithdrawRecordExportHistoryTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.rvWithdrawRecordExportHistory.layoutManager = LinearLayoutManager(this)
        mBinding.rvWithdrawRecordExportHistory.adapter =
            CommonRecyclerAdapter<ItemWithdrawRecordExportHistoryBinding, ExportHistoryEntity>(
                R.layout.item_withdraw_record_export_history,
                BR.item
            ) { _, _, _ -> }
        mBinding.rvWithdrawRecordExportHistory.requestData = object :
            CommonRefreshRecyclerView.OnRequestDataListener<ExportHistoryEntity>() {
            override fun requestData(
                isRefresh: Boolean,
                page: Int,
                pageSize: Int,
                callBack: (responseList: ResponseList<out ExportHistoryEntity>?) -> Unit
            ) {
                mViewModel.requestExportHistory(page, pageSize, callBack)
            }
        }
    }

    override fun initData() {
        mBinding.rvWithdrawRecordExportHistory.requestRefresh()
    }
}