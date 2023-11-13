package com.yunshang.haile_manager_android.ui.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.entities.WithdrawCalculateEntity
import com.yunshang.haile_manager_android.databinding.DialogWithdrawBinding

/**
 * Title : 常用的dialog
 * Author: Lsy
 * Date: 2023/4/4 09:55
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class WithdrawDialog private constructor(private val builder: Builder) : AppCompatDialogFragment() {
    private val WITHDRAW_TAG = "withdraw_tag"
    private lateinit var mBinding: DialogWithdrawBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogWithdrawBinding.inflate(inflater, container, false)
        // 背景图
        dialog?.window?.setBackgroundDrawableResource(R.drawable.shape_sffffff_r8)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        //宽高
        dialog?.window?.setLayout(
            resources.getDimensionPixelOffset(R.dimen.common_dialog_w),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //内容
        mBinding.tvWithdrawDialogAmount.text = builder.withdrawCalculate.totalAmount
        mBinding.tvWithdrawDialogServiceCharge.text = builder.withdrawCalculate.feeAmount
        mBinding.tvWithdrawDialogRate.text = "${builder.withdrawCalculate.cashOutRate * 1.0 / 100}%"
        mBinding.tvWithdrawDialogArrivalAmount.text = StringUtils.getString(R.string.unit_money) + builder.withdrawCalculate.realAmount

        //拒绝按钮
        mBinding.btnWithdrawDialogNo.setOnClickListener {
            dismiss()
        }

        //确定按钮
        mBinding.btnWithdrawDialogYes.setOnClickListener {
            dismiss()
            builder.positiveClickListener?.onClick(it)
        }
    }

    /**
     * 默认显示
     */
    fun show(manager: FragmentManager) {
        show(manager, WITHDRAW_TAG)
    }

    internal class Builder(val withdrawCalculate: WithdrawCalculateEntity) {

        // 同意按钮文本
        var positiveClickListener: OnClickListener? = null

        /**
         * 构建
         */
        fun build(): WithdrawDialog = WithdrawDialog(this)
    }
}