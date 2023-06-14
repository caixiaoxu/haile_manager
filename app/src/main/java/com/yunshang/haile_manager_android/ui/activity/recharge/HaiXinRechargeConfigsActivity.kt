package com.yunshang.haile_manager_android.ui.activity.recharge

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.HaiXinRechargeConfigsViewModel
import com.yunshang.haile_manager_android.databinding.ActivityHaixinRechargeConfigsBinding
import com.yunshang.haile_manager_android.databinding.IncludePersonalItemBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class HaiXinRechargeConfigsActivity :
    BaseBusinessActivity<ActivityHaixinRechargeConfigsBinding, HaiXinRechargeConfigsViewModel>(
        HaiXinRechargeConfigsViewModel::class.java, BR.vm
    ) {
    override fun layoutId(): Int = R.layout.activity_haixin_recharge_configs

    override fun backBtn(): View = mBinding.barHaixinRechargeTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // items
        var group: LinearLayout? = null
        for (item in mViewModel.rechargeConfigItems) {
            if (null == item) {
                //null，把之前的加入布局，并创建新的group
                if (null != group && group.childCount > 0) {
                    mBinding.llHaixinRechargeItems.addView(group)
                }
                group = createItemGroup()
            } else {
                // 如果不显示，跳转
                if (!item.isShow){
                    continue
                }
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
                        startActivity(Intent(this@HaiXinRechargeConfigsActivity, item.clz).apply {
                            item.bundle?.let { bundle ->
                                putExtras(bundle)
                            }
                        })
                    }
                }

                if (group.childCount > 0) {
                    group.addView(View(this@HaiXinRechargeConfigsActivity).apply {
                        setBackgroundColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.dividing_line_color,
                                null
                            )
                        )
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            DimensionUtils.dip2px(this@HaiXinRechargeConfigsActivity, 0.5f)
                        )
                    })
                }

                group.addView(
                    mPersonalItemBinding.root, LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        resources.getDimensionPixelOffset(R.dimen.item_height)
                    )
                )
            }
        }
    }

    /**
     * 创建ItemGroup
     */
    private fun createItemGroup(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setBackgroundResource(R.drawable.shape_sffffff_r8)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, DimensionUtils.dip2px(this@HaiXinRechargeConfigsActivity, 8f))
        }
    }

    override fun initData() {
        mViewModel.requestData()
    }
}