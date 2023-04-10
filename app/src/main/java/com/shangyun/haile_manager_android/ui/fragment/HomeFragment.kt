package com.shangyun.haile_manager_android.ui.fragment

import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.ScreenUtils
import com.lsy.framelib.utils.StatusBarUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.HomeViewModel
import com.shangyun.haile_manager_android.data.entities.MessageEntity
import com.shangyun.haile_manager_android.databinding.FragmentHomeBinding
import com.shangyun.haile_manager_android.databinding.IncludeHomeFuncItemBinding
import com.shangyun.haile_manager_android.databinding.IncludeHomeLastMsgItemBinding
import com.shangyun.haile_manager_android.utils.UserPermissionUtils

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
class HomeFragment : BaseBusinessFragment<FragmentHomeBinding, HomeViewModel>() {

    private val mHomeViewModel by lazy {
        getFragmentViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.fragment_home

    override fun getVM(): HomeViewModel = mHomeViewModel

    override fun initView() {

        mBinding.vm = mHomeViewModel
        mBinding.shared = mSharedViewModel
        //设置顶部距离
        val layoutParams = mBinding.bgHomeTitle.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topMargin = StatusBarUtils.getStatusBarHeight()
        mBinding.bgHomeTitle.layoutParams = layoutParams
    }

    override fun initEvent() {
        super.initEvent()

        // 消息列表 
        mHomeViewModel.lastMsgList.observe(this) { list ->
            list?.let {

                mBinding.llLastMsgList.removeAllViews()
                val lastMsg = it.take(2)
                val layoutInflater = LayoutInflater.from(context)
                lastMsg.forEach { msg ->
                    val mMsgItemBinding = DataBindingUtil.inflate<IncludeHomeLastMsgItemBinding>(
                        layoutInflater,
                        R.layout.include_home_last_msg_item,
                        null, false
                    )
                    mMsgItemBinding.lifecycleOwner = this
                    mMsgItemBinding.root.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        DimensionUtils.dip2px(requireContext(), 30f)
                    )
                    mMsgItemBinding.message = msg
                    mBinding.llLastMsgList.addView(mMsgItemBinding.root)
                }
            }
        }

        // 功能管理
        mHomeViewModel.funcList.observe(this) { list ->
            refreshFuncArea(list, mBinding.llFuncArea, mBinding.glFuncArea)
        }
        // 营销中心
        mHomeViewModel.marketingList.observe(this) { list ->
            refreshFuncArea(list, mBinding.llMarketingArea, mBinding.glMarketingArea)
        }
        // 资金管理
        mHomeViewModel.capitalList.observe(this) { list ->
            refreshFuncArea(list, mBinding.llCapitalArea, mBinding.glCapitalArea)
        }

        // 设备权限
        mSharedViewModel.hasDevicePermission.observe(this) {
            mHomeViewModel.funcList.value?.let { list ->
                mHomeViewModel.funcList.value = list.apply { this[0].isShow = it }
            }
        }
        // 门店权限
        mSharedViewModel.hasShopPermission.observe(this) {
            mHomeViewModel.funcList.value?.let { list ->
                mHomeViewModel.funcList.value = list.apply { this[1].isShow = it }
            }
        }
        // 订单权限
        mSharedViewModel.hasOrderPermission.observe(this) {
            mHomeViewModel.funcList.value?.let { list ->
                mHomeViewModel.funcList.value = list.apply { this[2].isShow = it }
            }
        }
        // 人员权限
        mSharedViewModel.hasPersonPermission.observe(this) {
            mHomeViewModel.funcList.value?.let { list ->
                mHomeViewModel.funcList.value = list.apply { this[3].isShow = it }
            }
        }
        // 优惠权限
        mSharedViewModel.hasMarketingPermission.observe(this) {
            mHomeViewModel.marketingList.value?.let { list ->
                mHomeViewModel.marketingList.value = list.apply { this[0].isShow = it }
            }
        }
        // 优惠权限
        mSharedViewModel.hasDistributionPermission.observe(this) {
            mHomeViewModel.capitalList.value?.let { list ->
                mHomeViewModel.capitalList.value = list.apply { this[0].isShow = it }
            }
        }
    }

    /**
     * 刷新功能区域界面
     */
    private fun refreshFuncArea(
        list: List<HomeViewModel.FunItem>,
        llArea: LinearLayout,
        glArea: GridLayout
    ) {
        // 根据权限过滤
        val filterList = list.filter { it.isShow }
        // 如果都没有就不显示
        llArea.visibility = if (filterList.isEmpty()) View.GONE else View.VISIBLE

        val layoutInflater = LayoutInflater.from(context)
        // 清除子View
        glArea.removeAllViews()
        // 计算宽度，(屏幕宽-边距)/列数
        val funcW =
            (ScreenUtils.screenWidth - 2 * DimensionUtils.dip2px(
                requireContext(),
                12f
            )) / glArea.columnCount

        // 边距
        val marginT = DimensionUtils.dip2px(requireContext(), 12f)
        filterList.forEach { item ->
            // 加载子view
            val mFuncAreaBinding = DataBindingUtil.inflate<IncludeHomeFuncItemBinding>(
                layoutInflater,
                R.layout.include_home_func_item,
                null, false
            )
            mFuncAreaBinding.lifecycleOwner = this
            mFuncAreaBinding.root.layoutParams = LinearLayout.LayoutParams(
                funcW,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = marginT
            }
            // 点击事件
            mFuncAreaBinding.root.setOnClickListener {
                startActivity(Intent(requireContext(), item.clz))
            }
            // 数据
            mFuncAreaBinding.funcItem = item
            glArea.addView(mFuncAreaBinding.root)
        }
    }

    override fun initData() {
        mHomeViewModel.requestHomeData()
    }
}