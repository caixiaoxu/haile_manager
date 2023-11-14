package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import androidx.core.content.ContextCompat
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.WithdrawRecordExportViewModel
import com.yunshang.haile_manager_android.databinding.ActivityWithdrawRecordExportBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import timber.log.Timber
import java.util.*

class WithdrawRecordExportActivity :
    BaseBusinessActivity<ActivityWithdrawRecordExportBinding, WithdrawRecordExportViewModel>(
        WithdrawRecordExportViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_withdraw_record_export

    override fun backBtn(): View = mBinding.barWithdrawRecordTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()
        mViewModel.jump.observe(this) {
            startActivity(
                Intent(
                    this@WithdrawRecordExportActivity,
                    WithdrawRecordExportHistoryActivity::class.java
                )
            )
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.barWithdrawRecordTitle.getRightBtn().run {
            setText(R.string.export_history)
            textSize = 14f
            setTextColor(
                ContextCompat.getColor(
                    this@WithdrawRecordExportActivity,
                    R.color.common_txt_color
                )
            )
            setOnClickListener {
                startActivity(
                    Intent(
                        this@WithdrawRecordExportActivity,
                        WithdrawRecordExportHistoryActivity::class.java
                    )
                )
            }
        }

        mBinding.clWithdrawRecordExportStartTime.setOnClickListener {
            DateSelectorDialog.Builder().apply {
                maxDate = Calendar.getInstance().apply { time = mViewModel.endTime.value ?: Date() }
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        Timber.i("----选择的开始日期${DateTimeUtils.formatDateTime(date1, "yyyy-MM-dd")}")
                        //更换时间
                        mViewModel.startTime.value = date1
                    }
                }
            }.build().show(supportFragmentManager, mViewModel.startTime.value)
        }
        mBinding.clWithdrawRecordExportEndTime.setOnClickListener {
            DateSelectorDialog.Builder().apply {
                mViewModel.startTime.value?.let {
                    minDate = Calendar.getInstance().apply {
                        time = it
                    }
                }
                maxDate = Calendar.getInstance().apply { time = Date() }
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        Timber.i("----选择的开始日期${DateTimeUtils.formatDateTime(date1, "yyyy-MM-dd")}")
                        //更换时间
                        mViewModel.endTime.value = date1
                    }
                }
            }.build().show(supportFragmentManager, mViewModel.endTime.value)
        }
    }

    override fun initData() {
    }
}