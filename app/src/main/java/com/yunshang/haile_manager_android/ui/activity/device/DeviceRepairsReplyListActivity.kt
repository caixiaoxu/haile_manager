package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.weight.SingleTapTextView
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceRepairsReplyListViewModel
import com.yunshang.haile_manager_android.business.vm.DeviceRepairsReplyViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.common.SearchType
import com.yunshang.haile_manager_android.data.entities.DeviceRepairsEntity
import com.yunshang.haile_manager_android.databinding.ActivityDeviceRepairsReplyListBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceRepairsBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.BitmapUtils
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView

class DeviceRepairsReplyListActivity :
    BaseBusinessActivity<ActivityDeviceRepairsReplyListBinding, DeviceRepairsReplyListViewModel>(
        DeviceRepairsReplyListViewModel::class.java, BR.vm
    ) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemDeviceRepairsBinding, DeviceRepairsEntity>(
            R.layout.item_device_repairs,
            BR.item
        ) { mItemBinding, _, item ->
            mViewModel.isBatch.observe(this) {
                mItemBinding?.isBatch = it
            }
            mItemBinding?.root?.setOnClickListener {
                if (true == mViewModel.isBatch.value) {
                    item.selected = !item.selected
                    refreshSelectBatchNum()
                } else {
                }
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_device_repairs_reply_list

    override fun backBtn(): View = mBinding.barDeviceRepairsReplyListTitle.getBackBtn()

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        mBinding.barDeviceRepairsReplyListTitle.getRightBtn(false).run {
            mViewModel.isBatch.observe(this@DeviceRepairsReplyListActivity) {
                setText(if (it) R.string.cancel else R.string.batch_reply)
            }
            textSize = 14f
            setTextColor(
                ContextCompat.getColor(
                    this@DeviceRepairsReplyListActivity,
                    R.color.color_black_85
                )
            )
            setOnClickListener {
                mViewModel.isBatch.value = !(mViewModel.isBatch.value ?: false).apply {
                    if (!this) {
                        resetSelectBatchNum()
                    }
                }
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        initRightBtn()

        val navigators = listOf<String>(
            "全部",""
        )

        mBinding.indicatorMineOrderStatus.navigator = CommonNavigator(this).apply {
            adapter = object : CommonNavigatorAdapter() {
                override fun getCount(): Int = 3

                override fun getTitleView(
                    context: Context?,
                    index: Int
                ): IPagerTitleView {
                    return SimplePagerTitleView(context).apply {
                        normalColor = ContextCompat.getColor(
                            this@DeviceRepairsReplyListActivity,
                            R.color.color_black_65
                        )
                        selectedColor = ContextCompat.getColor(
                            this@DeviceRepairsReplyListActivity,
                            R.color.color_black_85
                        )
                        list[index].run {
                            text = title
                            setOnClickListener {
                                mViewModel.curOrderStatus.value = value
                                onPageSelected(index)
                                notifyDataSetChanged()
                            }
                        }
                    }
                }

                override fun getIndicator(context: Context?): IPagerIndicator {
                    return LinePagerIndicator(context).apply {
                        mode = LinePagerIndicator.MODE_WRAP_CONTENT
                        setColors(
                            ContextCompat.getColor(
                                this@DeviceRepairsReplyListActivity,
                                R.color.color_black_85
                            )
                        )
                    }
                }
            }
        }

        mBinding.rvDeviceRepairsReplyList.layoutManager = LinearLayoutManager(this)
        mBinding.rvDeviceRepairsReplyList.adapter = mAdapter
        mBinding.rvDeviceRepairsReplyList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<DeviceRepairsEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out DeviceRepairsEntity>?) -> Unit
                ) {
                    mViewModel.requestDeviceRepairsList(page, pageSize, callBack)
                }
            }

        mBinding.cbDeviceRepairsReplyListAll.setOnCheckClickListener {
            if (!mBinding.cbDeviceRepairsReplyListAll.isChecked) {
                selectAll()
            } else {
                resetSelectBatchNum()
            }

            true
        }

        mBinding.btnDeviceRepairsReplyListReply.setOnClickListener {

        }
    }

    private fun selectAll() {
        mAdapter.list.forEach {
            it.selected = 10 == it.replyStatus
        }
        mViewModel.refreshSelectBatchNum(mAdapter.list)
    }

    private fun resetSelectBatchNum() {
        mAdapter.list.forEach {
            it.selected = false
        }
        mViewModel.refreshSelectBatchNum(mAdapter.list)
    }

    private fun refreshSelectBatchNum() {
        mViewModel.refreshSelectBatchNum(mAdapter.list)
    }

    override fun initData() {

    }
}