package com.lsy.framelib.ui.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * Title : fragment基类(绑定界面)
 * Author: Lsy
 * Date: 2023/4/10 09:40
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
abstract class BaseBindingFragment<T : ViewDataBinding> : BaseFragment() {

    protected lateinit var mBinding: T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = DataBindingUtil.inflate(
            inflater,
            layoutId(),
            container,
            false
        )
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

    /**
     * 根布局
     */
    abstract fun layoutId(): Int

}