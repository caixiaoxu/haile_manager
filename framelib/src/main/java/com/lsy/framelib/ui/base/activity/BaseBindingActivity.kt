package com.lsy.framelib.ui.base.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * Title : Activity基类(构建布局DataBinding)
 * Author: Lsy
 * Date: 2023/3/16 14:25
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
abstract class BaseBindingActivity<T : ViewDataBinding> : BaseActivity() {
    protected val mBinding: T by lazy {
        DataBindingUtil.setContentView(this, layoutId())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.lifecycleOwner = this
        initStatusBarColor(mBinding.root)
    }

    /**
     * 根布局
     */
    abstract fun layoutId(): Int
}