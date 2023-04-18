package com.shangyun.haile_manager_android.ui.activity.login

import android.content.Intent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.AppManager
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.ChangeUserViewModel
import com.shangyun.haile_manager_android.data.entities.ChangeUserEntity
import com.shangyun.haile_manager_android.databinding.ActivityChangeUserBinding
import com.shangyun.haile_manager_android.databinding.ItemChangeAccountBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.activity.MainActivity
import com.shangyun.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter

class ChangeUserActivity :
    BaseBusinessActivity<ActivityChangeUserBinding, ChangeUserViewModel>() {

    private val mChangeUserViewModel by lazy {
        getActivityViewModelProvider(this)[ChangeUserViewModel::class.java]
    }

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemChangeAccountBinding, ChangeUserEntity>(
            R.layout.item_change_account, BR.user,
        ) { mBinding, data ->
            mBinding?.root?.setOnClickListener {
                if (!data.isCurUser()) {
                    mChangeUserViewModel.changeUser(data, mSharedViewModel)
                }
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_change_user

    override fun getVM(): ChangeUserViewModel = mChangeUserViewModel

    override fun backBtn(): View = mBinding.barActionTitle.getBackBtn()

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

        mBinding.rvAccountList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvAccountList.adapter = mAdapter
    }

    override fun initEvent() {
        super.initEvent()
        mChangeUserViewModel.userList.observe(this) {
            mAdapter.refreshList(it)
        }

        mChangeUserViewModel.jump.observe(this) {
            AppManager.finishAllActivity()
            when (it) {
                1 -> {
                    startActivity(Intent(this@ChangeUserActivity, MainActivity::class.java))
                }
                2-> {
                    startActivity(Intent(this@ChangeUserActivity, LoginActivity::class.java))
                }
            }
        }
    }

    override fun initData() {
    }
}