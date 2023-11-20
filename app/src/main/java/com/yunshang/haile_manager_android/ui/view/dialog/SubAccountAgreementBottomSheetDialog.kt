package com.yunshang.haile_manager_android.ui.view.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.common.Constants
import com.yunshang.haile_manager_android.databinding.DialogSubAccountAgreementBinding


/**
 * Title : 日期选择Dialog
 * Author: Lsy
 * Date: 2023/4/11 09:47
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class SubAccountAgreementBottomSheetDialog private constructor(private val builder: Builder) :
    BottomSheetDialogFragment() {
    private val SUB_ACCOUNT_AGREEMENT_BOTTOM_SHEET_TAG = "sub_account_agreement_bottom_sheet_tag"
    lateinit var mBinding: DialogSubAccountAgreementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CommonBottomSheetDialogStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            if (this is BottomSheetDialog) {
                setOnShowListener {
                    behavior.isHideable = false
                    // dialog上还有一层viewGroup，需要设置成透明
                    (requireView().parent as ViewGroup).setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogSubAccountAgreementBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.tvSubAccountAgreementClose.setOnClickListener {
            dismiss()
        }
        mBinding.wvSubAccountAgreement.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                view.loadUrl(request.url.toString())
                return true
            }
        }
        mBinding.wvSubAccountAgreement.settings.javaScriptEnabled = true
        mBinding.wvSubAccountAgreement.loadUrl(
            Constants.SUB_ACCOUNT_AGREEMENT_URL + (builder.name ?: "")
        )

        mBinding.cbSubAccountAgreement.setOnCheckedChangeListener { _, isChecked ->
            mBinding.btnSubAccountAgreement.isEnabled = isChecked
        }

        mBinding.btnSubAccountAgreement.setOnClickListener {
            dismiss()
            builder.onValueSureListener?.onValue(mBinding.cbSubAccountAgreement.isChecked)
        }
    }

    /**
     * 默认显示
     */
    fun show(manager: FragmentManager) {
        //不可取消
        isCancelable = false
        show(manager, SUB_ACCOUNT_AGREEMENT_BOTTOM_SHEET_TAG)
    }


    internal class Builder(val name: String?) {

        // 选择监听
        var onValueSureListener: OnValueSureListener? = null

        /**
         * 构建
         */
        fun build(): SubAccountAgreementBottomSheetDialog =
            SubAccountAgreementBottomSheetDialog(this)
    }

    interface OnValueSureListener {
        fun onValue(isChecked: Boolean)
    }
}