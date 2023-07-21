package com.yunshang.haile_manager_android.ui.activity.notice

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.NoticeCreateViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams.SearchSelectTypeParam
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.NoticeTemplateEntity
import com.yunshang.haile_manager_android.data.entities.StaffRoleEntity
import com.yunshang.haile_manager_android.databinding.ActivityNoticeCreateBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.setItemContent
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import timber.log.Timber
import java.util.Date

class NoticeCreateActivity :
    BaseBusinessActivity<ActivityNoticeCreateBinding, NoticeCreateViewModel>(
        NoticeCreateViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_notice_create

    override fun backBtn(): View = mBinding.barNoticeCreateTitle.getBackBtn()

    private var bottomSheetDialog: CommonBottomSheetDialog<NoticeTemplateEntity>? = null

    // 店铺选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                SearchSelectTypeParam.ShopResultCode -> {
                    it.data?.let { intent ->
                        intent.getStringExtra(SearchSelectTypeParam.ResultData)?.let { json ->
                            GsonUtils.json2List(json, SearchSelectParam::class.java)
                                ?.let { selected ->
                                    mViewModel.takeChargeShop.value = selected
                                }
                        }
                    }
                }
            }
        }

    override fun initEvent() {
        super.initEvent()
        mViewModel.noticeId = intent.getIntExtra(NoticeDetailActivity.NoticeId, -1)
        mViewModel.templateList.observe(this) {
            if (!it.isNullOrEmpty()) {
                bottomSheetDialog = CommonBottomSheetDialog.Builder(
                    StringUtils.getString(R.string.notice_template),
                    it
                )
                    .apply {
                        onValueSureListener =
                            object :
                                CommonBottomSheetDialog.OnValueSureListener<NoticeTemplateEntity> {
                                override fun onValue(data: NoticeTemplateEntity?) {
                                    mViewModel.templateid = data?.id!!
                                    mViewModel.templatename.value = data?.name
                                }
                            }
                    }.build()
            }
        }
        mViewModel.jump.observe(this) {
            finish()
        }
        mViewModel.notice.observe(this) {
            var beans = ArrayList<SearchSelectParam>()
            it.noticeShopDtos.forEach {
                var bean = SearchSelectParam(it.shopId, it.shopName)
                beans.add(bean)
            }
            mViewModel.takeChargeShop.value = beans
            mViewModel.createTime.value = DateTimeUtils.formatDateTimeForStr(
                it.startTime,
                "yyyy-MM-dd HH:mm"
            ) + "~" + DateTimeUtils.formatDateTimeForStr(it.endTime, "yyyy-MM-dd HH:mm")
            mViewModel.showTime.value = DateTimeUtils.formatDateTimeForStr(
                it.templateStartTime,
                "yyyy-MM-dd HH:mm"
            ) + "~" + DateTimeUtils.formatDateTimeForStr(it.templateEndTime, "yyyy-MM-dd HH:mm")
            mViewModel.templateid = it.templateId
            mViewModel.templatename.value = it.templateName
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.itemNoticeCreateSelectshop.onSelectedEvent = {
            startSearchSelect.launch(Intent(
                this@NoticeCreateActivity, SearchSelectRadioActivity::class.java
            ).apply {
                putExtras(putExtras(SearchSelectTypeParam.pack(SearchSelectTypeParam.SearchSelectTypeTakeChargeShop)))
            })
        }

        mBinding.itemNoticeCreateTime.onSelectedEvent = {
            val dailog = DateSelectorDialog.Builder().apply {
                selectModel = 1
                showModel = 8
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        mViewModel.createTime.value = DateTimeUtils.formatDateTime(
                            date1,
                            "yyyy-MM-dd HH:mm"
                        ) + "~" + DateTimeUtils.formatDateTime(date2, "yyyy-MM-dd HH:mm")
                    }
                }
            }.build()
            dailog.show(supportFragmentManager)
        }

        mBinding.itemNoticeCreateShowtime.onSelectedEvent = {
            val dailog = DateSelectorDialog.Builder().apply {
                selectModel = 1
                showModel = 8
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        mViewModel.showTime.value = DateTimeUtils.formatDateTime(
                            date1,
                            "yyyy-MM-dd HH:mm"
                        ) + "~" + DateTimeUtils.formatDateTime(date2, "yyyy-MM-dd HH:mm")
                    }
                }
            }.build()
            dailog.show(supportFragmentManager)
        }

        mBinding.itemNoticeCreateTemplate.onSelectedEvent = {
            bottomSheetDialog?.show(supportFragmentManager)
        }

        mViewModel.notice.observe(this) {
            mBinding.itemNoticeCreateTemplate.setItemContent(it.templateName)
            mBinding.itemNoticeCreateTime.setItemContent(it.time())
            mBinding.itemNoticeCreateShowtime.setItemContent(it.showtime())
            mBinding.itemNoticeCreateSelectshop.setItemContent(it.shopnames())
        }


    }

    override fun initData() {
        mViewModel.requestNoticeList()
        if (mViewModel.noticeId != -1) {
            mViewModel.requestNoticeDetailData()
        }
    }
}