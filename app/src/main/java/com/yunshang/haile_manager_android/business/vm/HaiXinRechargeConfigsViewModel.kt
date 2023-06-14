package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.HaiXinService
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.ui.activity.recharge.*
import com.yunshang.haile_manager_android.utils.UserPermissionUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/14 18:13
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class HaiXinRechargeConfigsViewModel : BaseViewModel() {
    val mHaiXinRepo = ApiRepository.apiClient(HaiXinService::class.java)

    var refundQrCode: String = ""

    val rechargeConfigItems = arrayOf(
        PersonalViewModel.PersonalItem(
            R.mipmap.icon_recharge_total_main,
            R.string.recharge_total,
            null,
            MutableLiveData<String>(),
            HaiXinRechargeListActivity::class.java,
            isShow = UserPermissionUtils.hasVipRechargePermission()
        ),
        PersonalViewModel.PersonalItem(
            R.mipmap.icon_scheme_config_main,
            R.string.scheme_config,
            null,
            null,
            HaiXinSchemeConfigsActivity::class.java,
            isShow = UserPermissionUtils.hasVipListPermission()
        ),
        null,
        PersonalViewModel.PersonalItem(
            R.mipmap.icon_recharge_accounts_main,
            R.string.recharge_accounts,
            null,
            null,
            HaiXinRechargeAccountsActivity::class.java,
            isShow = UserPermissionUtils.hasVipRechargeUserPermission()
        ),
        null,
        PersonalViewModel.PersonalItem(
            R.mipmap.icon_refund_record_main,
            R.string.refund_record,
            null,
            null,
            HaiXinRefundRecordActivity::class.java,
            isShow = UserPermissionUtils.hasVipRefundListPermission()
        ),
        PersonalViewModel.PersonalItem(
            R.mipmap.icon_refund_qrcode_main,
            R.string.refund_qrcode,
            null,
            null,
            isShow = UserPermissionUtils.hasVipRefundCodePermission()
        ),
        null,
        PersonalViewModel.PersonalItem(
            R.mipmap.icon_haixin_recharge_main,
            R.string.haixin_recharge,
            null,
            null,
            HaiXinRechargeActivity::class.java,
        ),
        null,
    )

    fun requestData() {
        launch({
            ApiRepository.dealApiResult(
                mHaiXinRepo.requestHaiXinTotal(
                    ApiRepository.createRequestBody(
                        hashMapOf()
                    )
                )
            )?.let {
                rechargeConfigItems[0]!!.value!!.postValue("${it.tokenCoinAmount}")
            }
            ApiRepository.dealApiResult(
                mHaiXinRepo.requestRefundQrCode()
            )?.let {
                refundQrCode = it
            }
        })
    }
}