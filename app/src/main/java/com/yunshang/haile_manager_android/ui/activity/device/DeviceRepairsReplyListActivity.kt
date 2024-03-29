package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.DeviceRepairsReplyListViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.DeviceRepairsEntity
import com.yunshang.haile_manager_android.data.extend.isGreaterThan0
import com.yunshang.haile_manager_android.databinding.ActivityDeviceRepairsReplyListBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceRepairsReplyListBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.order.OrderManagerActivity
import com.yunshang.haile_manager_android.ui.view.IndicatorPagerTitleView
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.StringUtils
import com.yunshang.haile_manager_android.utils.UserPermissionUtils
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

class DeviceRepairsReplyListActivity :
    BaseBusinessActivity<ActivityDeviceRepairsReplyListBinding, DeviceRepairsReplyListViewModel>(
        DeviceRepairsReplyListViewModel::class.java, BR.vm
    ) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemDeviceRepairsReplyListBinding, DeviceRepairsEntity>(
            R.layout.item_device_repairs_reply_list,
            BR.item
        ) { mItemBinding, _, item ->
            mViewModel.isBatch.observe(this) {
                mItemBinding?.isBatch = it
            }
            mItemBinding?.cbDeviceRepairsReplyListSelect?.setOnCheckedChangeListener { _, isChecked ->
                item.selected = isChecked
                refreshSelectBatchNum()
            }

            mItemBinding?.tvDeviceRepairsUser?.movementMethod = LinkMovementMethod.getInstance()
            mItemBinding?.tvDeviceRepairsUser?.highlightColor = Color.TRANSPARENT
            mItemBinding?.tvDeviceRepairsUser?.text =
                (item.userAccount?.let {
                    StringUtils.formatMultiStyleStr(
                        "${com.lsy.framelib.utils.StringUtils.getString(R.string.repairs_user)}：$it",
                        arrayOf(
                            ForegroundColorSpan(
                                ContextCompat.getColor(
                                    this@DeviceRepairsReplyListActivity,
                                    R.color.colorPrimary
                                )
                            ),
                            object : ClickableSpan() {
                                override fun onClick(view: View) {
                                    showPhoneOperateDialog(it)
                                }

                                override fun updateDrawState(ds: TextPaint) {
                                    //去掉下划线
                                    ds.isUnderlineText = false
                                }
                            },
                        ), 5, it.length + 5
                    )
                } ?: "")

            mItemBinding?.root?.setOnClickListener {
                if (true == mViewModel.isBatch.value) {
                    item.selected = !item.selected
                    refreshSelectBatchNum()
                    return@setOnClickListener
                }
                item.id?.let { id ->
                    startActivity(
                        Intent(
                            this@DeviceRepairsReplyListActivity,
                            DeviceRepairsReplyActivity::class.java
                        ).apply {
                            putExtras(IntentParams.CommonParams.pack(id))
                        })
                }
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_device_repairs_reply_list

    override fun backBtn(): View = mBinding.barDeviceRepairsReplyListTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.deviceRepairs =
            IntentParams.DeviceRepairsReplyListParams.parseDeviceRepairs(intent)
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.isBatch.observe(this) {
            if (!it) {
                resetSelectBatchNum()
            }
        }

        LiveDataBus.with(BusEvents.DEVICE_FAULT_REPAIRS_REPLY_STATUS)?.observe(this) {
            mBinding.rvDeviceRepairsReplyList.requestRefresh()
        }
    }

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
                mViewModel.isBatch.value = !(mViewModel.isBatch.value ?: false)
            }
        }
    }

    private fun showPhoneOperateDialog(phone: String) {
        CommonBottomSheetDialog.Builder(
            phone, arrayListOf(
                SearchSelectParam(0, "拨号"),
                SearchSelectParam(1, "复制"),
                SearchSelectParam(2, "用户订单"),
            )
        ) { dialogBinding ->
            dialogBinding.tvCommonDialogTitle.textSize = 14f
            dialogBinding.tvCommonDialogTitle.setTextColor(
                ContextCompat.getColor(
                    this@DeviceRepairsReplyListActivity,
                    R.color.color_black_45
                )
            )
        }.apply {
            mustSelect = false
            showClose = false
            isClickClose = true
            onValueSureListener =
                object :
                    CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                    override fun onValue(data: SearchSelectParam?) {
                        when (data?.id) {
                            0 -> {
                                val intent = Intent(Intent.ACTION_DIAL)
                                intent.data = Uri.parse("tel:${phone}")
                                startActivity(intent)
                            }
                            1 -> StringUtils.copyToShear(phone)
                            2 -> {
                                startActivity(
                                    Intent(
                                        this@DeviceRepairsReplyListActivity,
                                        OrderManagerActivity::class.java
                                    ).apply {
                                        putExtras(
                                            IntentParams.OrderManagerParams.pack(
                                                2,
                                                phone = phone
                                            )
                                        )
                                    })
                            }
                            else -> {}
                        }
                    }
                }
        }.build().show(supportFragmentManager)
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        if (UserPermissionUtils.hasRepairsReplyPermission()) {
            initRightBtn()
        }

        mBinding.btnDeviceRepairsReplyListDeviceDetail.setOnClickListener {
            startActivity(
                Intent(
                    this@DeviceRepairsReplyListActivity,
                    DeviceDetailActivity::class.java
                ).apply {
                    putExtra(DeviceDetailActivity.GoodsId, mViewModel.deviceRepairs?.goodsId)
                }
            )
        }

        val navigators = listOf(
            SearchSelectParam(0, "全部"),
            SearchSelectParam(10, "未回复"),
            SearchSelectParam(20, "已回复"),
        )

        mBinding.indicatorMineOrderStatus.navigator = CommonNavigator(this).apply {
            adapter = object : CommonNavigatorAdapter() {
                override fun getCount(): Int = navigators.size

                override fun getTitleView(
                    context: Context?,
                    index: Int
                ): IPagerTitleView {
                    return IndicatorPagerTitleView(context).apply {
                        normalFontSize = 14f
                        selectFontSize = 14f
                        normalColor = ContextCompat.getColor(
                            this@DeviceRepairsReplyListActivity,
                            R.color.color_black_65
                        )
                        selectedColor = ContextCompat.getColor(
                            this@DeviceRepairsReplyListActivity,
                            R.color.color_black_85
                        )
                        navigators[index].run {
                            text = name
                            if (1 == index) {
                                mViewModel.noRelyNum.observe(this@DeviceRepairsReplyListActivity) {
                                    text = "$name${if (it.isGreaterThan0()) " $it" else ""}"
                                }
                            }
                            setOnClickListener {
                                mViewModel.curStatus = if (0 == id) null else id
                                onPageSelected(index)
                                notifyDataSetChanged()
                                mBinding.rvDeviceRepairsReplyList.requestRefresh()
                            }
                        }
                    }
                }

                override fun getIndicator(context: Context?): IPagerIndicator {
                    return LinePagerIndicator(context).apply {
                        mode = LinePagerIndicator.MODE_EXACTLY
                        lineWidth = DimensionUtils.dip2px(this@DeviceRepairsReplyListActivity, 20f)
                            .toFloat()
                        lineHeight =
                            DimensionUtils.dip2px(this@DeviceRepairsReplyListActivity, 3f).toFloat()
                        setColors(
                            ContextCompat.getColor(
                                this@DeviceRepairsReplyListActivity,
                                R.color.color_black_85
                            )
                        )
                    }
                }
            }

            isAdjustMode = true
        }

        mBinding.indicatorMineOrderStatus.onPageSelected(1)

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
            val ids = mAdapter.list.filter { item -> item.selected }.mapNotNull { item -> item.id }
                .toIntArray()
            if (ids.isNotEmpty()) {
                startActivity(
                    Intent(
                        this@DeviceRepairsReplyListActivity,
                        DeviceRepairsBatchReplyActivity::class.java
                    ).apply {
                        putExtras(IntentParams.DeviceFaultRepairsParams.pack(ids))
                    }
                )
                mViewModel.isBatch.value = false
            }
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
        mBinding.rvDeviceRepairsReplyList.requestRefresh()
    }
}