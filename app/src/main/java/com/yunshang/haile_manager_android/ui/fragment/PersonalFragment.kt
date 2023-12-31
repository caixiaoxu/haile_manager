package com.yunshang.haile_manager_android.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout.LayoutParams
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StatusBarUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.PersonalViewModel
import com.yunshang.haile_manager_android.data.arguments.StaffParam
import com.yunshang.haile_manager_android.databinding.FragmentPersonalBinding
import com.yunshang.haile_manager_android.databinding.IncludePersonalItemBinding
import com.yunshang.haile_manager_android.ui.activity.personal.ChangeOrganizationActivity
import com.yunshang.haile_manager_android.ui.activity.personal.PersonalInfoActivity
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.visibility
import com.yunshang.haile_manager_android.utils.UserPermissionUtils

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
class PersonalFragment : BaseBusinessFragment<FragmentPersonalBinding, PersonalViewModel>(
    PersonalViewModel::class.java,
    BR.vm
) {
    override fun layoutId(): Int = R.layout.fragment_personal

    override fun initView() {
        mBinding.shared = mSharedViewModel

        //设置顶部距离
        mBinding.llPersonal.setPadding(
            DimensionUtils.dip2px(dipValue = 12f),
            StatusBarUtils.getStatusBarHeight(),
            DimensionUtils.dip2px(dipValue = 12f),
            0
        )

        mBinding.clPersonalInfo.setOnClickListener {
            startActivity(Intent(context, PersonalInfoActivity::class.java))
        }

        mBinding.btnOrganizationChange.setOnClickListener {
            startActivity(Intent(context, ChangeOrganizationActivity::class.java))
        }

        buildPersonalItems()
    }

    /**
     * 构建个人item
     */
    private fun buildPersonalItems(){
        mBinding.llPersonalItems.removeAllViews()
        var group: LinearLayoutCompat? = null
        for (item in mViewModel.personalItems) {
            if (null == item) {
                //null，把之前的加入布局，并创建新的group
                if (null != group) {
                    mBinding.llPersonalItems.addView(group)
                    group.visibility(group.children.any { view -> View.VISIBLE == view.visibility })
                }
                group = createItemGroup()
            } else {
                // 不为空，加载子布局加入group
                if (null == group) {
                    group = createItemGroup()
                }
                val mPersonalItemBinding = DataBindingUtil.inflate<IncludePersonalItemBinding>(
                    layoutInflater,
                    R.layout.include_personal_item,
                    null, false
                )
                mPersonalItemBinding.lifecycleOwner = this
                mPersonalItemBinding.item = item
                mPersonalItemBinding.root.setOnClickListener {
                    item.clz?.let {
                        startActivity(Intent(requireContext(), item.clz).apply {
                            item.bundle?.let { bundle ->
                                putExtras(bundle)
                            }
                        })
                    }
                }
                when (item.title) {
                    R.string.bank_card -> {
                        mPersonalItemBinding.root.visibility(UserPermissionUtils.hasWalletBankPermission())
                        mSharedViewModel.hasWalletBankPermission.observe(this) {
                            mPersonalItemBinding.root.visibility(it)
                            group.visibility(group.children.any { view -> View.VISIBLE == view.visibility })
                        }
                    }
                    R.string.real_name -> {
                        mPersonalItemBinding.tvPersonalItemValue.textSize = 10f
                        mPersonalItemBinding.tvPersonalItemValue.setTextColor(Color.WHITE)
                        val pV = DimensionUtils.dip2px(requireContext(), 2f)
                        val pH = DimensionUtils.dip2px(requireContext(), 8f)
                        mPersonalItemBinding.tvPersonalItemValue.setPadding(pH, pV, pH, pV)
                        mPersonalItemBinding.tvPersonalItemValue.setBackgroundResource(R.drawable.shape_sff3b30_r9)
                    }
                    R.string.income_calendar -> {
                        val isSpecialRole =
                            StaffParam.isSpecialRole(mSharedViewModel.userInfo.value?.userInfo?.tagName)
                        mPersonalItemBinding.root.visibility(!isSpecialRole && UserPermissionUtils.hasProfitCalendarPermission())
                    }
                    R.string.income_statistics -> {
                        val isSpecialRole =
                            StaffParam.isSpecialRole(mSharedViewModel.userInfo.value?.userInfo?.tagName)
                        mPersonalItemBinding.root.visibility(!isSpecialRole && UserPermissionUtils.hasProfitDetailPermission())
                    }
                }

                group.addView(
                    mPersonalItemBinding.root, LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        requireContext().resources.getDimensionPixelOffset(R.dimen.item_height)
                    )
                )
            }
        }
    }

    /**
     * 创建ItemGroup
     */
    private fun createItemGroup(): LinearLayoutCompat = LinearLayoutCompat(requireContext()).apply {
        orientation = LinearLayoutCompat.VERTICAL
        setBackgroundResource(R.drawable.shape_sffffff_r8)
        dividerDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.divder_efefef)
        showDividers = LinearLayoutCompat.SHOW_DIVIDER_MIDDLE
        layoutParams = LinearLayoutCompat.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, DimensionUtils.dip2px(requireContext(), 12f), 0, 0)
        }
    }

    override fun initEvent() {
        super.initEvent()

        LiveDataBus.with(BusEvents.BALANCE_STATUS)?.observe(this) {
            mViewModel.requestBalanceAsync()
        }

        mSharedViewModel.hasUserPermission.observe(this){
            buildPersonalItems()
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.requestRealNameAuthAsync()
    }

    override fun initData() {
        mSharedViewModel.requestUserInfoAsync()
        mViewModel.requestData()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mViewModel.requestBalanceAsync()
            mViewModel.requestRealNameAuthAsync()
        }
    }

}