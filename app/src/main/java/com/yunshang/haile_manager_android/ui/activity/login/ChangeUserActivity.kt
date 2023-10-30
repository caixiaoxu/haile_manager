package com.yunshang.haile_manager_android.ui.activity.login

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.AppManager
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.ChangeUserViewModel
import com.yunshang.haile_manager_android.data.entities.ChangeUserEntity
import com.yunshang.haile_manager_android.data.model.SPRepository
import com.yunshang.haile_manager_android.databinding.ActivityChangeUserBinding
import com.yunshang.haile_manager_android.databinding.ItemChangeAccountBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.MainActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter

class ChangeUserActivity :
    BaseBusinessActivity<ActivityChangeUserBinding, ChangeUserViewModel>(
        ChangeUserViewModel::class.java,
        BR.vm
    ) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemChangeAccountBinding, ChangeUserEntity>(
            R.layout.item_change_account, BR.user,
        ) { mBinding, _, data ->
            mBinding?.root?.setOnClickListener {
                if (!data.isCurUser()) {
                    mViewModel.changeUser(data, mSharedViewModel)
                }
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_change_user

    override fun backBtn(): View = mBinding.barActionTitle.getBackBtn()

    override fun initView() {
        mBinding.rvAccountList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvAccountList.adapter = mAdapter

        mBinding.llChangeUserAdd.setOnClickListener {
            SPRepository.cleaLoginUserInfo()
            AppManager.finishAllActivity()
            startActivity(Intent(this@ChangeUserActivity, LoginForPasswordActivity::class.java))
        }
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.userList.observe(this) {
            mAdapter.refreshList(it)
        }

        mViewModel.jump.observe(this) {
            AppManager.finishAllActivity()
            when (it) {
                1 -> {
                    startActivity(Intent(this@ChangeUserActivity, MainActivity::class.java))
                }
                2 -> {
                    startActivity(Intent(this@ChangeUserActivity, LoginActivity::class.java))
                }
            }
        }
    }

    override fun initData() {
    }
}