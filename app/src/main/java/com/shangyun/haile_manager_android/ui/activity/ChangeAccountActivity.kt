package com.shangyun.haile_manager_android.ui.activity

import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.ChangeAccountViewModel
import com.shangyun.haile_manager_android.databinding.ActivityChangeAccountBinding

class ChangeAccountActivity :
    BaseBusinessActivity<ActivityChangeAccountBinding, ChangeAccountViewModel>() {

    private val mChangeAccountViewModel by lazy {
        getActivityViewModelProvider(this)[ChangeAccountViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_change_account


    override fun getVM(): ChangeAccountViewModel = mChangeAccountViewModel

    override fun initView() {

        ResourcesCompat.getDrawable(
            resources,
            R.drawable.divder_size8,
            null
        )?.let { drawable ->
            mBinding.rvAccountList.addItemDecoration(
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
                    setDrawable(drawable)
                })
        }

        mBinding.rvAccountList.adapter =

    }

    override fun initData() {
    }
}