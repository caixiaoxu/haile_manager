package com.yunshang.haile_manager_android.ui.activity.notice

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.webkit.WebSettings
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.NoticeDetailViewModel
import com.yunshang.haile_manager_android.databinding.ActivityNoticeDetailBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog

class NoticeDetailActivity :
    BaseBusinessActivity<ActivityNoticeDetailBinding, NoticeDetailViewModel>(
        NoticeDetailViewModel::class.java, BR.vm
    ) {

    companion object {
        const val NoticeId = "noticeId"
    }

    override fun layoutId(): Int = R.layout.activity_notice_detail

    override fun backBtn(): View = mBinding.barNoticeDetailTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()
        mViewModel.noticeId = intent.getIntExtra(NoticeDetailActivity.NoticeId, -1)
        // 修改成功
        LiveDataBus.with(BusEvents.NOTICE_DETAIL_CHANGE)?.observe(this) {
            mViewModel.requestNoticeDetailData()
        }
        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.tvNoticeEnd.setOnClickListener {
            CommonDialog.Builder(StringUtils.getString(R.string.notice_end_message)).apply {
                negativeTxt = StringUtils.getString(R.string.cancel)
                setPositiveButton(StringUtils.getString(R.string.sure)) {
                    mViewModel.requestNoticeDelete(it)
                }
            }.build()
                .show(supportFragmentManager)
        }
        mBinding.tvNoticeEdit.setOnClickListener {
            startActivity(
                Intent(
                    this@NoticeDetailActivity,
                    NoticeCreateActivity::class.java
                ).apply {
                    putExtra(NoticeId, mViewModel.noticeId)
                })
        }

        initWebView()
        mViewModel.notice.observe(this) {
            mBinding.wbNoticeContent.loadData(it.templateContent, "text/html; charset=UTF-8", null)
        }
    }

    override fun initData() {
        mViewModel.requestNoticeDetailData()
    }

    private fun initWebView() {
        mBinding.wbNoticeContent.settings.run {
            defaultTextEncodingName = "utf-8"
            builtInZoomControls = false
            domStorageEnabled = true
            allowFileAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }


}