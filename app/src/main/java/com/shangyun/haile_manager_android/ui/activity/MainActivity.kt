package com.shangyun.haile_manager_android.ui.activity

import android.os.Bundle
import android.view.View
import com.lsy.framelib.ui.base.BaseVMActivity
import com.shangyun.haile_manager_android.business.vm.MainViewModel
import com.shangyun.haile_manager_android.databinding.ActivityMainBinding

class MainActivity : BaseBusinessActivity<MainViewModel>() {

    private val mMainViewModel by lazy {
        getActivityViewModelProvider(this)[MainViewModel::class.java]
    }

    private val mMainbinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mMainbinding.btnRequestNetwork.setOnClickListener {
            mMainViewModel.requestData()
        }
    }

    override fun rooView(): View = mMainbinding.root
    override fun initView() {
    }

    override fun initData() {
    }

    override fun getVM(): MainViewModel = mMainViewModel

}