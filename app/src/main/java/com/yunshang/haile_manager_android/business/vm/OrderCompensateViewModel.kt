package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.business.apiService.DiscountsService
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/10 11:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class OrderCompensateViewModel : BaseViewModel() {
    private val mDiscountRepo = ApiRepository.apiClient(DiscountsService::class.java)
    var couponAmount: Double = 0.00
    var couponShopId: Int = -1
    var couponShopName: String? = ""
        get() = field ?: ""
    var couponDeviceTypeIds: ArrayList<Int>? = arrayListOf()
    var couponDeviceTypeName: String? = ""
        get() = field ?: ""
    var buyerId: Int = -1
    var buyerPhone: String? = ""
    var orderNo: String? = ""

    // 有效天数
    val indate: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 优惠券数量
    val couponNum: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(indate) {
            value = checkSubmit()
        }
        addSource(couponNum) {
            value = checkSubmit()
        }
    }

    /**
     * 检测是否可提交
     */
    private fun checkSubmit(): Boolean =
        !indate.value.isNullOrEmpty() && !couponNum.value.isNullOrEmpty()

    fun submit(view: View) {
        if (-1 == couponShopId || -1 == buyerId || orderNo.isNullOrEmpty() || buyerPhone.isNullOrEmpty() || couponDeviceTypeIds.isNullOrEmpty()) {
            return
        }

        val indateValue = try {
            indate.value!!.toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            SToast.showToast(view.context, "请输入正确的有效期")
            return
        }

        val couponNumValue = try {
            couponNum.value!!.toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            SToast.showToast(view.context, "请输入正确的补偿数量")
            return
        }

        launch(
            {
                ApiRepository.dealApiResult(
                    mDiscountRepo.activateCompensate(
                        ApiRepository.createRequestBody(
                            hashMapOf(
                                "couponType" to 1,//1 满减券，3 折扣券，4 兑换券
                                "shopIds" to arrayOf(couponShopId),//店铺id
                                "machineParentTypeIds" to couponDeviceTypeIds!!,//设备id
                                "orderReachPrice" to 0,//满X元可用
                                "isCalculateTimeFromActivation" to true, //是否领取后生效
                                "activateTimeType" to 3,//1 年，2 月，3 日，4 时，5 分，6 秒
                                "activateTime" to indateValue,//领取后生效有效期
                                "reduce" to couponAmount,//减x元
                                "organizationType" to 3,//1 全部店铺，2 指定运营商，3 指定店铺
                                "userCouponCountList" to arrayOf(
                                    hashMapOf(
                                        "phone" to buyerPhone!!,
                                        "userId" to buyerId,
                                        "count" to couponNumValue,
                                    )
                                ),//人员信息
                                "orderNo" to orderNo!!,//订单号
                            )
                        )
                    )
                )
                withContext(Dispatchers.Main) {
                    SToast.showToast(view.context, "发放成功")
                }
                jump.postValue(0)
            })
    }
}