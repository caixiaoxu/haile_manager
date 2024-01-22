package com.yunshang.haile_manager_android.ui.activity.message

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.MessageListViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.common.CommonKeyValueEntity
import com.yunshang.haile_manager_android.data.entities.MessageContentEntity
import com.yunshang.haile_manager_android.data.entities.MessageContentType2ClickAttr
import com.yunshang.haile_manager_android.data.entities.MessageContentType2Item
import com.yunshang.haile_manager_android.data.entities.MessageEntity
import com.yunshang.haile_manager_android.databinding.ActivityMessageListBinding
import com.yunshang.haile_manager_android.databinding.ItemMessageListBinding
import com.yunshang.haile_manager_android.databinding.ItemMessageListInfosBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.device.DeviceDetailActivity
import com.yunshang.haile_manager_android.ui.view.MultiTypeItemView
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.visibility
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.StringUtils
import com.yunshang.haile_manager_android.utils.scheme.SchemeURLHelper

class MessageListActivity :
    BaseBusinessActivity<ActivityMessageListBinding, MessageListViewModel>(MessageListViewModel::class.java) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemMessageListBinding, MessageEntity>(
            R.layout.item_message_list, BR.item,
        ) { mItemBinding, pos, item ->

            if (1 == item.contentVersion) {
                item.contentEntity?.let { content ->
                    mItemBinding?.llMessageListInfos?.buildChild<ItemMessageListInfosBinding, CommonKeyValueEntity>(
                        content.items()
                    ) { _, childBinding, data ->
                        childBinding.title = data.title
                        childBinding.value = data.value
                    }
                }
            } else {
                item.contentType2Entity?.let { content ->
                    refreshMsgItemClickAttr(
                        content.top,
                        mItemBinding?.tvMessageListType2Title,
                        100,
                        content.cardClickAttr
                    )
                    refreshMsgItemClickAttr(
                        content.topRight,
                        mItemBinding?.tvMessageListType2Status,
                        100,
                        content.cardClickAttr
                    )
                    mItemBinding?.llMessageListType2Info?.buildChild<ItemMessageListInfosBinding, MessageContentType2Item>(
                        content.body?.keyTextList
                    ) { _, childBinding, data ->
                        childBinding.title = data.key
                        childBinding.value = data.text
                        if (true == data.clickAttr?.canClick)
                            childBinding.itemMessageListShopName.contentView.setTextColor(
                                ContextCompat.getColor(
                                    this@MessageListActivity,
                                    R.color.colorPrimary
                                )
                            )
                        refreshMsgItemClickAttr(
                            data,
                            childBinding.root,
                            200,
                            content.cardClickAttr
                        )
                    }
                    refreshMsgItemClickAttr(
                        content.bottom,
                        mItemBinding?.tvMessageListType2Bottom,
                        300,
                        content.cardClickAttr
                    )
                }
            }

            if (0 == pos) {
                mItemBinding?.root?.setPadding(
                    0,
                    DimensionUtils.dip2px(this@MessageListActivity, 12f),
                    0,
                    0
                )
            }
            mItemBinding?.root?.setOnClickListener {
                if (1 == item.contentVersion) {
                    if (item.subtype == "merchant:device:fault") {
                        (item.contentEntity as? MessageContentEntity)?.goodsId?.let { goodsId ->
                            startActivity(Intent(
                                this@MessageListActivity,
                                DeviceDetailActivity::class.java
                            ).apply {
                                putExtra(DeviceDetailActivity.GoodsId, goodsId)
                            })
                        }
                    }
                } else {
                    if (true == item.contentType2Entity?.cardClickAttr?.canClick
                        && true == item.contentType2Entity?.cardClickAttr?.redirectRegions?.contains(
                            0
                        )
                    ) {
                        item.contentType2Entity?.cardClickAttr?.redirectUrlAndroid?.let { linkUrl ->
                            SchemeURLHelper.parseSchemeURL(
                                this@MessageListActivity, linkUrl
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * 每条消息的点击事件处理
     *
     * @param type2Item 数据
     * @param itemView 操作控件
     * @param clickRegion 100 顶部，200 主体，300 底部，0全部
     * @param cardClickAttr 总事件
     */
    private fun refreshMsgItemClickAttr(
        type2Item: MessageContentType2Item?,
        itemView: View?,
        clickRegion: Int,
        cardClickAttr: MessageContentType2ClickAttr?
    ) {
        var txtColor = try {
            Color.parseColor(type2Item?.color)
        } catch (e: Exception) {
            null
        }

        txtColor?.let {
            if (itemView is MultiTypeItemView) {
                itemView.contentView.setTextColor(txtColor)
            } else if (itemView is TextView) {
                itemView.setTextColor(txtColor)
            }
        }

        val type =
            if (true == type2Item?.clickAttr?.canClick) 0
            else if (true == cardClickAttr?.canClick
                && true == cardClickAttr.redirectRegions?.contains(clickRegion)
            ) 1
            else -1

        if (type >= 0) {
            val clickType =
                if (1 == type) 200 else type2Item?.clickAttr?.clickType
            val redirectUrl =
                if (1 == type) cardClickAttr?.redirectUrlAndroid else type2Item?.clickAttr?.redirectUrlAndroid
            if (itemView is MultiTypeItemView) {
                itemView.onSelectedEvent = {
                    msgItemClickAttr(
                        type2Item?.text,
                        clickType,
                        redirectUrl
                    )
                }
            } else {
                itemView?.setOnClickListener {
                    msgItemClickAttr(
                        type2Item?.text,
                        clickType,
                        redirectUrl
                    )
                }
            }
        }
        itemView?.visibility(!type2Item?.text.isNullOrEmpty())
    }

    private fun msgItemClickAttr(
        text: String?,
        clickType: Int?,
        redirectUrl: String?,
    ) {
        if (100 == clickType) {
            text?.let { title ->
                StringUtils.copyToShear(title)
            }
        } else {
            redirectUrl?.let { linkUrl ->
                SchemeURLHelper.parseSchemeURL(
                    this@MessageListActivity, linkUrl
                )
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_message_list

    override fun backBtn(): View = mBinding.barMessageListTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.typeId =
            IntentParams.MessageListParams.parseTypeId(intent).let { if (-1 == it) null else it }
        mViewModel.subtypeId =
            IntentParams.MessageListParams.parseSubtypeId(intent).let { if (-1 == it) null else it }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        IntentParams.MessageListParams.parseMessageName(intent)?.let {
            mBinding.barMessageListTitle.setTitle(it)
        }

        mBinding.rvMessageList.layoutManager = LinearLayoutManager(this)
        ResourcesCompat.getDrawable(resources, R.drawable.divide_size16, null)?.let {
            mBinding.rvMessageList.addItemDecoration(
                DividerItemDecoration(
                    this@MessageListActivity,
                    DividerItemDecoration.VERTICAL
                ).apply {
                    setDrawable(it)
                })
        }
        mBinding.rvMessageList.adapter = mAdapter
        mBinding.rvMessageList.requestData = object :
            CommonRefreshRecyclerView.OnRequestDataListener<MessageEntity>() {
            override fun requestData(
                isRefresh: Boolean,
                page: Int,
                pageSize: Int,
                callBack: (responseList: ResponseList<out MessageEntity>?) -> Unit
            ) {
                mViewModel.requestMessageList(page, pageSize, callBack)
            }
        }
    }

    override fun initData() {
        mBinding.rvMessageList.requestRefresh()
    }
}