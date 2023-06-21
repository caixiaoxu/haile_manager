package com.yunshang.haile_manager_android.ui.activity.message

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.MessageCenterViewModel
import com.yunshang.haile_manager_android.data.entities.DeviceEntity
import com.yunshang.haile_manager_android.databinding.ActivityMessageCenterBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView

class MessageCenterActivity :
    BaseBusinessActivity<ActivityMessageCenterBinding, MessageCenterViewModel>(
        MessageCenterViewModel::class.java
    ) {
    override fun layoutId(): Int = R.layout.activity_message_center

    override fun backBtn(): View = mBinding.barMessageCenterTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.barMessageCenterTitle.getRightArea().run {
            removeAllViews()

            addView(AppCompatTextView(this@MessageCenterActivity).apply {
                setText(R.string.clean_unread)
                textSize = 14f
                setTextColor(
                    ContextCompat.getColor(
                        this@MessageCenterActivity,
                        R.color.common_txt_color
                    )
                )
                typeface = Typeface.DEFAULT_BOLD
            }, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            addView(AppCompatTextView(this@MessageCenterActivity).apply {
                setText(R.string.setting)
                textSize = 14f
                setTextColor(
                    ContextCompat.getColor(
                        this@MessageCenterActivity,
                        R.color.common_txt_color
                    )
                )
                typeface = Typeface.DEFAULT_BOLD
                val ph = DimensionUtils.dip2px(this@MessageCenterActivity, 16f)
                setPadding(ph, 0, ph, 0)
            }, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        }

        mBinding.rvMessageList.layoutManager = LinearLayoutManager(this)
        mBinding.rvMessageList.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            ).apply {
                ResourcesCompat.getDrawable(resources, R.drawable.shape_bottom_stroke_dividing_efefef_mlr16, null)?.let {
                    setDrawable(it)
                }
            })

        mBinding.rvMessageList.adapter = mAdapter
    }

    override fun initData() {
        mViewModel.requestMessageList()
    }
}