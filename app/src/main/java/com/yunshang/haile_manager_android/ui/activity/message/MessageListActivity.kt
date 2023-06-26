package com.yunshang.haile_manager_android.ui.activity.message

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.MessageListViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.common.CommonKeyValueEntity
import com.yunshang.haile_manager_android.data.entities.MessageContentEntity
import com.yunshang.haile_manager_android.data.entities.MessageEntity
import com.yunshang.haile_manager_android.databinding.ActivityMessageListBinding
import com.yunshang.haile_manager_android.databinding.ItemMessageListBinding
import com.yunshang.haile_manager_android.databinding.ItemMessageListInfosBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.device.DeviceDetailActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView

class MessageListActivity :
    BaseBusinessActivity<ActivityMessageListBinding, MessageListViewModel>(MessageListViewModel::class.java) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemMessageListBinding, MessageEntity>(
            R.layout.item_message_list, BR.item,
        ) { mItemBinding, _, item ->
            item.contentEntity?.let { content ->
                mItemBinding?.llMessageListInfos?.buildChild<ItemMessageListInfosBinding, CommonKeyValueEntity>(
                    content.items()
                ) { _, childBinding, data ->
                    childBinding.item = data
                }
            }
            mItemBinding?.root?.setOnClickListener {
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
            }
        }
    }


    override fun layoutId(): Int = R.layout.activity_message_list

    override fun backBtn(): View = mBinding.barMessageListTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.typeId = IntentParams.MessageListParams.parseTypeId(intent)
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