package com.yunshang.haile_manager_android.ui.activity.recharge

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.HaiXinRechargeViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.databinding.ActivityHaixinRechargeBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog

class HaiXinRechargeActivity :
    BaseBusinessActivity<ActivityHaixinRechargeBinding, HaiXinRechargeViewModel>(
        HaiXinRechargeViewModel::class.java, BR.vm
    ) {

    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)?.let { json ->
                    GsonUtils.json2List(json, SearchSelectParam::class.java)?.let { selected ->
                        if (selected.isNotEmpty()) {
                            mViewModel.selectShop.value = selected[0]
                        }
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_haixin_recharge

    override fun backBtn(): View = mBinding.barHaixinRechargeTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()
        mViewModel.selectShop.observe(this) {
            mViewModel.shopChargeRate()
        }
        mViewModel.jump.observe(this) {
            CommonDialog.Builder("海星充值操作成功").apply {
                title = StringUtils.getString(R.string.tip)
                setNegativeButton("返回") {
                    finish()
                }
                positiveTxt = "继续充值"
            }.build().show(supportFragmentManager)
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.itemHaixinRechargeShop.onSelectedEvent = {
            startSearchSelect.launch(
                Intent(
                    this@HaiXinRechargeActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(IntentParams.SearchSelectTypeParam.pack(IntentParams.SearchSelectTypeParam.SearchSelectTypeShop))
                })
        }
    }

    override fun initData() {
    }
}