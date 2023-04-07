package com.shangyun.haile_manager_android.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.ui.base.BaseVMFragment
import com.lsy.framelib.utils.StatusBarUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.databinding.FragmentHomeBinding

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/7 13:58
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class HomeFragment : BaseVMFragment() {
    private lateinit var mBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )

        //设置顶部距离
        val layoutParams = mBinding.bgHomeTitle.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topMargin = StatusBarUtils.getStatusBarHeight()
        mBinding.bgHomeTitle.layoutParams = layoutParams
        return mBinding.root
    }
}