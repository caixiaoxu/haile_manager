package com.lsy.framelib.ui.base.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

/**
 * Title : fragment 基类
 * Author: Lsy
 * Date: 2023/4/10 09:34
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
abstract class BaseFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 回退按钮
        backBtn()?.setOnClickListener { onBackListener() }
    }

    /**
     * 回退按钮
     */
    open fun backBtn(): View? = null

    /**
     * 默认的回退事件
     */
    protected open fun onBackListener() {
        if (1 < childFragmentManager.fragments.size) {
            childFragmentManager.popBackStack()
        } else {
            requireActivity().finish()
        }
    }
}