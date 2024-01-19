package com.yunshang.haile_manager_android.ui.view.dialog

import android.os.Bundle
import android.text.InputFilter.LengthFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.databinding.DialogCancelContentBinding

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/12 17:10
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
</desc></version></time></author> */
class CancelContentDialog private constructor(private val builder: CancelContentDialog.Builder) :
    AppCompatDialogFragment() {
    private val CANCEL_CONTENT_TAG = "cancel_content"
    private lateinit var mBinding: DialogCancelContentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogCancelContentBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.shape_sffffff_r8)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.tvCancelContentTitle.text = builder.title
        mBinding.etCancelContentCause.hint = builder.hint
        mBinding.etCancelContentCause.filters = arrayOf(
            LengthFilter(builder.contentLength)
        )
        mBinding.tvCancelContentCauseNum.text = "0/${builder.contentLength}"
        mBinding.etCancelContentCause.addTextChangedListener {
            mBinding.tvCancelContentCauseNum.text =
                "${it.toString().length}/${builder.contentLength}"
        }
        //拒绝按钮
        if (builder.isNegativeShow) {
            builder.negativeTxt?.let { mBinding.tvCancelNo.text = it }
            mBinding.tvCancelNo.setOnClickListener {
                if (!builder.isPositiveShow && 0 == (mBinding.etCancelContentCause.text?.length
                        ?: 0)
                ) {
                    SToast.showToast(requireContext(), builder.hint)
                    return@setOnClickListener
                }
                dismiss()
                builder.negativeClickListener?.invoke(mBinding.etCancelContentCause.text.toString())
            }
            mBinding.tvCancelNo.visibility = View.VISIBLE
        } else {
            mBinding.tvCancelNo.visibility = View.GONE
        }

        //确定按钮
        if (builder.isPositiveShow) {
            builder.positiveTxt?.let { mBinding.tvCancelYes.text = it }
            mBinding.tvCancelYes.setOnClickListener {
                if (0 == (mBinding.etCancelContentCause.text?.length ?: 0)) {
                    SToast.showToast(requireContext(), builder.hint)
                    return@setOnClickListener
                }
                dismiss()
                builder.positiveClickListener?.invoke(mBinding.etCancelContentCause.text.toString())
            }
            mBinding.tvCancelYes.visibility = View.VISIBLE
        } else {
            mBinding.tvCancelYes.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        //宽高
        dialog?.window?.setLayout(
            DimensionUtils.dip2px(requireContext(), 311f),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    fun show(manager: FragmentManager) {
        isCancelable = builder.isCancelable
        show(manager, CANCEL_CONTENT_TAG)
    }

    internal class Builder(val title: String, val hint: String) {
        var contentLength: Int = 30

        // 拒绝按钮文本
        var negativeTxt: String? = null
        var negativeClickListener: ((content: String) -> Unit)? = null
        var isNegativeShow: Boolean = true

        // 同意按钮文本
        var positiveTxt: String? = null
        var positiveClickListener: ((content: String) -> Unit)? = null
        var isPositiveShow: Boolean = true

        // 不可取消
        var isCancelable = false

        /**
         * 设置拒绝按钮
         */
        fun setNegativeButton(txt: String, clickListener: ((content: String) -> Unit)) {
            negativeTxt = txt
            negativeClickListener = clickListener
            isNegativeShow = true
        }

        /**
         * 设置确定按钮
         */
        fun setPositiveButton(txt: String, clickListener: ((content: String) -> Unit)) {
            positiveTxt = txt
            positiveClickListener = clickListener
            isPositiveShow = true
        }

        /**
         * 构建
         */
        fun build(): CancelContentDialog = CancelContentDialog(this)
    }
}