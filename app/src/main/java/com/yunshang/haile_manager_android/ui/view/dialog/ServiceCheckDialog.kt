package com.yunshang.haile_manager_android.ui.view.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.databinding.DialogServiceCheckBinding

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
class ServiceCheckDialog(val dismissListener: OnClickListener) : AppCompatDialogFragment() {
    private val SERVICE_CHECK_TAG = "service_check_tag"
    private lateinit var mBinding: DialogServiceCheckBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogServiceCheckBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        mBinding.wvServiceCheck.settings.javaScriptEnabled = true
        mBinding.wvServiceCheck.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                view.loadUrl(request.url.toString())
                return true
            }
        }
        mBinding.wvServiceCheck.loadUrl("https://notice.haier-ioc.com/notice.html ")
        mBinding.btnServiceCheck.setOnClickListener {
            dismiss()
            dismissListener.onClick(it)
        }
    }

    override fun onResume() {
        super.onResume()
        //宽高
        dialog?.window?.setLayout(
            DimensionUtils.dip2px(requireContext(), 311f),
            DimensionUtils.dip2px(requireContext(), 394f)
        )
    }

    fun show(manager: FragmentManager) {
        isCancelable = true
        show(manager, SERVICE_CHECK_TAG)
    }
}