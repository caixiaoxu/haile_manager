package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.CategoryService
import com.yunshang.haile_manager_android.business.apiService.DiscountsService
import com.yunshang.haile_manager_android.data.entities.CouponDetailEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/14 18:05
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class CouponDetailViewModel: BaseViewModel() {
    private val mDiscountsRepo = ApiRepository.apiClient(DiscountsService::class.java)

    var couponId:Int = -1

    val couponDetail:MutableLiveData<CouponDetailEntity> by lazy {
        MutableLiveData()
    }

    fun requestCouponDetail(){
        if (couponId <=0) return

        launch({
            ApiRepository.dealApiResult(
                mDiscountsRepo.requestCouponDetail(couponId)
            )?.let {
                couponDetail.postValue(it)
            }
        })
    }
}