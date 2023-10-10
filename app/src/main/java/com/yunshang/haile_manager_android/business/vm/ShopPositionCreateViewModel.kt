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
import com.yunshang.haile_manager_android.data.arguments.BusinessHourEntity
import com.yunshang.haile_manager_android.data.arguments.ShopCreateParam
import com.yunshang.haile_manager_android.data.arguments.ShopPositionCreateParam
import com.yunshang.haile_manager_android.data.entities.SchoolSelectEntity
import com.yunshang.haile_manager_android.data.entities.ShopDetailEntity
import com.yunshang.haile_manager_android.data.entities.ShopPositionDetailEntity
import com.yunshang.haile_manager_android.data.entities.ShopTypeEntity
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
     * 把点位详情的数据赋值到编辑参数中
     */
    fun updatePositionDetail(positionDetail: ShopPositionDetailEntity?) {
        positionDetail?.let {
            positionParam.value = ShopPositionCreateParam(
                it.id,
                it.name,
                it.shopId,
                serviceTelephone = it.serviceTelephone,
                sex = it.sex
            ).apply {
                it.shopName?.let { name ->
                    shopName = name
                }
                if (null != it.lat && null != it.lng && null != it.address) {
                    changeAddress(
                        it.lat,
                        it.lng,
                        it.address
                    )
                }
                changeWorkTime(it.workTimeArr(), it.workTime)
            }
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

        val body = ApiRepository.createRequestBody(GsonUtils.any2Json(positionParam.value))

        launch({
            ApiRepository.dealApiResult(
                if (null == positionParam.value?.id) {
                    mShopRepo.createPosition(body)
                } else {
                    mShopRepo.updatePosition(body)
                }
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(
                    v.context,
                    if (null == positionParam.value?.id) R.string.add_success else R.string.update_success
                )
            }

            LiveDataBus.post(BusEvents.SHOP_LIST_STATUS, true)
            LiveDataBus.post(BusEvents.PT_DETAILS_STATUS, true)
            jump.postValue(0)
        })
    }
}