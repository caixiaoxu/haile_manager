package com.shangyun.haile_manager_android.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.ui.base.BaseVMFragment
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.databinding.FragmentHomeBinding
import com.shangyun.haile_manager_android.databinding.FragmentPersonalBinding

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
class PersonalFragment : BaseVMFragment() {
    private lateinit var mBinding: FragmentPersonalBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_personal,
            container,
            false
        )
        return mBinding.root
    }
}