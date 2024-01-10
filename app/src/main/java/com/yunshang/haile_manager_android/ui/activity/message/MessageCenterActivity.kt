package com.yunshang.haile_manager_android.ui.activity.message

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.MessageCenterViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityMessageCenterBinding
import com.yunshang.haile_manager_android.databinding.ItemMessageCenterBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter

class MessageCenterActivity :
    BaseBusinessActivity<ActivityMessageCenterBinding, MessageCenterViewModel>(
        MessageCenterViewModel::class.java, BR.vm
    ) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemMessageCenterBinding, MessageCenterViewModel.MessageCenterEntity>(
            R.layout.item_message_center,
            BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.root?.setOnClickListener {
                mViewModel.readAllMessage(item.typeId)
                LiveDataBus.post(BusEvents.MESSAGE_READ_STATUS, true)
                startActivity(
                    Intent(
                        this@MessageCenterActivity,
                        MessageListActivity::class.java
                    ).apply {
                        putExtras(
                            IntentParams.MessageListParams.pack(
                                item.typeId,
                                item.id,
                                item.title
                            )
                        )
                    })
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_message_center

    override fun backBtn(): View = mBinding.barMessageCenterTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()
        mViewModel.messageList.observe(this) {
            mAdapter.refreshList(it, true)
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.barMessageCenterTitle.getRightBtn(false).run {
            setText(R.string.setting)
            textSize = 17f
            setTextColor(
                ContextCompat.getColor(
                    this@MessageCenterActivity,
                    R.color.color_black_85
                )
            )
            setOnClickListener {
                startActivity(
                    Intent(
                        this@MessageCenterActivity,
                        MessageSettingActivity::class.java
                    )
                )
            }
        }

        mBinding.btnCleanUnreadMsg.setOnClickListener {
            mViewModel.readAllMessage()
        }

        mBinding.rvMessageList.layoutManager = LinearLayoutManager(this)
        mBinding.rvMessageList.adapter = mAdapter
    }

    override fun initData() {
        mViewModel.requestMessageList()
    }
}