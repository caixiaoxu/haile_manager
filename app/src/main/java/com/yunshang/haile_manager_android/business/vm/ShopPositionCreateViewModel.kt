package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.ShopService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.arguments.ShopPositionCreateParam
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/10/9 15:08
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class ShopPositionCreateViewModel : BaseViewModel() {
    private val mShopRepo = ApiRepository.apiClient(ShopService::class.java)

    val positionParam = MutableLiveData(ShopPositionCreateParam())

    fun requestShopDetail(callBack: (cityName: String?) -> Unit) {
        positionParam.value?.shopId?.let { shopId ->
            launch({
                val result = ApiRepository.dealApiResult(
                    mShopRepo.shopDetail(ApiRepository.createRequestBody(hashMapOf("id" to shopId)))
                )
                withContext(Dispatchers.Main) {
                    callBack(result?.cityName)
                }
            })
        } ?: run {
            callBack(null)
        }
    }

    /**
     * 提交
     */
    fun submit(v: View) {
        if (null == positionParam.value?.shopId) {
            SToast.showToast(v.context, "请先选择所属门店")
            return
        }
        if (positionParam.value?.name.isNullOrEmpty()) {
            SToast.showToast(v.context, "请先输入点位名称")
            return
        }
        if (null == positionParam.value?.lat || null == positionParam.value?.lng) {
            SToast.showToast(v.context, "请先选择定位")
            return
        }
        if (positionParam.value?.address.isNullOrEmpty()) {
            SToast.showToast(v.context, "请先输入详细位置")
            return
        }
        if (null == positionParam.value?.sex) {
            SToast.showToast(v.context, "请先选择适用性别")
            return
        }
        if (positionParam.value?.workTime.isNullOrEmpty()) {
            SToast.showToast(v.context, "请先选择营业时间")
            return
        }
        if (positionParam.value?.serviceTelephone.isNullOrEmpty()) {
            SToast.showToast(v.context, "请先输入客服电话")
            return
        }

        launch({
            ApiRepository.dealApiResult(
                mShopRepo.createPosition(
                    ApiRepository.createRequestBody(
                        GsonUtils.any2Json(positionParam.value)
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(v.context, R.string.create_success)
            }

            LiveDataBus.post(BusEvents.SHOP_LIST_STATUS, true)
            LiveDataBus.post(BusEvents.SHOP_DETAILS_STATUS, true)
            jump.postValue(0)
        })
    }
}