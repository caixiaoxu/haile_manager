package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.ScreenUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceRepairsReplyViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.ReplyDTOS
import com.yunshang.haile_manager_android.databinding.ActivityDeviceRepairsReplyBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceRepairsBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceRepairsReplyBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.PicBrowseActivity
import com.yunshang.haile_manager_android.ui.view.RoundImageView
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.loadImage
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.visibility

class DeviceRepairsReplyActivity :
    BaseBusinessActivity<ActivityDeviceRepairsReplyBinding, DeviceRepairsReplyViewModel>(
        DeviceRepairsReplyViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_device_repairs_reply

    override fun backBtn(): View = mBinding.barDeviceRepairsReplyTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.id = IntentParams.CommonParams.parseId(intent)
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.repairsDetails.observe(this) {
            it?.let { detail ->
                mBinding.clDeviceRepairsReplyFaultPic.removeViews(
                    1,
                    mBinding.clDeviceRepairsReplyFaultPic.childCount - 1
                )
                val picItemWH = (ScreenUtils.screenWidth - DimensionUtils.dip2px(
                    this@DeviceRepairsReplyActivity,
                    16f
                ) * 2 - DimensionUtils.dip2px(this@DeviceRepairsReplyActivity, 12f) * 3) / 4
                if (detail.pics.isNullOrEmpty()) {
                    mBinding.clDeviceRepairsReplyFaultPic.visibility(false)
                } else {
                    mBinding.clDeviceRepairsReplyFaultPic.visibility(true)
                    val picItemRadius =
                        DimensionUtils.dip2px(this@DeviceRepairsReplyActivity, 12f)
                    detail.pics.forEachIndexed { index, url ->
                        mBinding.clDeviceRepairsReplyFaultPic.addView(RoundImageView(this@DeviceRepairsReplyActivity).apply {
                            radius = picItemRadius.toFloat()
                            id = index + 1
                            loadImage(imgHeadUrl = url)
                            setOnClickListener {
                                startActivity(
                                    Intent(
                                        this@DeviceRepairsReplyActivity,
                                        PicBrowseActivity::class.java
                                    ).apply {
                                        putExtras(IntentParams.PicParams.pack(url))
                                    })
                            }
                        }, picItemWH, picItemWH)
                    }
                    mBinding.flowDeviceRepairsReplyFaultPic.referencedIds =
                        IntArray(detail.pics.size) { item -> item + 1 }
                }

                mBinding.llDeviceRepairsReplyList.buildChild<ItemDeviceRepairsReplyBinding, ReplyDTOS>(
                    it.replyDTOS,
                    start = 1
                ) { _, childBinding, data ->
                    childBinding.child = data
                }
            }
        }

        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
    }

    override fun initData() {
        mViewModel.requestData()
    }
}