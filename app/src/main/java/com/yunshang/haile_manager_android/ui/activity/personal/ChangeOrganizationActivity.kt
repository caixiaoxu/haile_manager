package com.yunshang.haile_manager_android.ui.activity.personal

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.ChangeOrganizationViewModel
import com.yunshang.haile_manager_android.data.entities.RoleEntity
import com.yunshang.haile_manager_android.data.model.SPRepository
import com.yunshang.haile_manager_android.databinding.ActivityChangeOrganizationBinding
import com.yunshang.haile_manager_android.databinding.ItemChangeOrganizationBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter

class ChangeOrganizationActivity :
    BaseBusinessActivity<ActivityChangeOrganizationBinding, ChangeOrganizationViewModel>(
        ChangeOrganizationViewModel::class.java, BR.vm
    ) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemChangeOrganizationBinding, RoleEntity>(
            R.layout.item_change_organization, BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.root?.setOnClickListener {
                mViewModel.swapUserLogin(item.id, mSharedViewModel) {
                    refreshAdapter()
                }
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_change_organization

    override fun backBtn(): View = mBinding.barChangeOrganizationTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()
        mViewModel.roleList.observe(this) {
            mAdapter.refreshList(it, true)
        }
    }

    private fun refreshAdapter() {
        mAdapter.notifyDataSetChanged()
    }


    override fun initView() {

        mBinding.rvChangeOrganizationList.layoutManager = LinearLayoutManager(this)
        mBinding.rvChangeOrganizationList.adapter = mAdapter
    }

    override fun initData() {
        mViewModel.requestRoleList()
    }
}