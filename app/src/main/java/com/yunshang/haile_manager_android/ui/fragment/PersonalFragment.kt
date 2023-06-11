package com.yunshang.haile_manager_android.ui.fragment

import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StatusBarUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.PersonalViewModel
import com.yunshang.haile_manager_android.databinding.FragmentPersonalBinding
import com.yunshang.haile_manager_android.databinding.IncludePersonalItemBinding
import com.yunshang.haile_manager_android.ui.activity.personal.PersonalInfoActivity

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

        // items
        var group: LinearLayout? = null
        for (item in mViewModel.personalItems) {
            if (null == item) {
                //null，把之前的加入布局，并创建新的group
                if (null != group) {
                    mBinding.llPersonalItems.addView(group)
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
                    startActivity(Intent(requireContext(), item.clz).apply {
                        item.bundle?.let { bundle ->
                            putExtras(bundle)
                        }
                    })
                }

                if (group.childCount > 0) {
                    group.addView(View(requireContext()).apply {
                        setBackgroundColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.dividing_line_color,
                                null
                            )
                        )
                        layoutParams = LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            DimensionUtils.dip2px(requireContext(), 0.5f)
                        )
                    })
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
    private fun createItemGroup(): LinearLayout = LinearLayout(requireContext()).apply {
        orientation = LinearLayout.VERTICAL
        setBackgroundResource(R.drawable.shape_sffffff_r8)
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, DimensionUtils.dip2px(requireContext(), 8f))
        }
    }

    override fun initEvent() {
        super.initEvent()
    }

    override fun initData() {
        mViewModel.requestData()
    }

}