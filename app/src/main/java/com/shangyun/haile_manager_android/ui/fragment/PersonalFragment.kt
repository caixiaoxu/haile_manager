package com.shangyun.haile_manager_android.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.ui.base.fragment.BaseVMFragment
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.HomeViewModel
import com.shangyun.haile_manager_android.business.vm.PersonalViewModel
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
class PersonalFragment : BaseBusinessFragment<FragmentPersonalBinding,PersonalViewModel>() {

    private val mPersonalViewModel by lazy {
        getFragmentViewModelProvider(this)[PersonalViewModel::class.java]
    }

    override fun layoutId(): Int =R.layout.fragment_personal

    override fun getVM(): PersonalViewModel =mPersonalViewModel

    override fun initView() {
    }

    override fun initData() {
    }

}