package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CategoryService
import com.yunshang.haile_manager_android.business.apiService.DiscountsService
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.CouponEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/12 15:44
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class IssueCouponsViewModel : BaseViewModel() {
    private val mDiscountsRepo = ApiRepository.apiClient(DiscountsService::class.java)
    private val mCategoryRepo = ApiRepository.apiClient(CategoryService::class.java)

    val coupon: MutableLiveData<CouponEntity> = MutableLiveData(CouponEntity())

    val couponTypeList =
        StringUtils.getStringArray(R.array.coupon_type).mapIndexed { index, s ->
            SearchSelectParam(index, s)
        }.filter { item -> !item.name.isNullOrEmpty() }


    // 设备类型
    val categoryList: MutableLiveData<MutableList<CategoryEntity>> by lazy {
        MutableLiveData()
    }

    /**
     * 请求设备类型
     */
    fun requestDeviceCategory() {
        launch({
            ApiRepository.dealApiResult(
                mCategoryRepo.category(1)
            )?.let {
                it.add(
                    0,
                    CategoryEntity(
                        id = 0,
                        name = StringUtils.getString(R.string.all_device)
                    ).apply {
                        onlyOne = true
                    })
                categoryList.postValue(it)
            }
        })
    }

    /**
     * 确认发券
     */
    fun sureIssueCoupon(v: View) {
        if (coupon.value?.issueCouponsUser.isNullOrEmpty()) {
            SToast.showToast(v.context, "请输入发送用户的手机号")
            return
        }
        if (null == coupon.value?.couponType || coupon.value!!.couponType!! <= 0) {
            SToast.showToast(v.context, "请选择券类型")
            return
        }
        if (1 == coupon.value?.couponType) {
            if (null == coupon.value?.reduce) {
                SToast.showToast(v.context, "请输入券金额")
                return
            }
            if (0.01 > coupon.value!!.reduce!! || 100 < coupon.value!!.reduce!!) {
                SToast.showToast(v.context, "请券金额范围为0.01-100.00")
                return
            }
            if (null == coupon.value?.orderReachPrice) {
                SToast.showToast(v.context, "请输入使用条件")
                return
            }
            if (0 > coupon.value!!.orderReachPrice!! || 100 < coupon.value!!.orderReachPrice!!) {
                SToast.showToast(v.context, "请使用条件范围为0-100.00")
                return
            }
        } else if (3 == coupon.value?.couponType) {
            if (null == coupon.value?.percentage) {
                SToast.showToast(v.context, "请输入券折扣")
                return
            }
            if (0.1 > coupon.value!!.percentage!! || 9.9 < coupon.value!!.percentage!!) {
                SToast.showToast(v.context, "请券折扣范围为0.1-9.9")
                return
            }
            if (null == coupon.value?.maxDiscountPrice) {
                SToast.showToast(v.context, "请输入减免上限")
                return
            }
            if (0.01 > coupon.value!!.maxDiscountPrice!! || 100 < coupon.value!!.maxDiscountPrice!!) {
                SToast.showToast(v.context, "请券折扣范围为0.01-100.00")
                return
            }
            if (null == coupon.value?.orderReachPrice) {
                SToast.showToast(v.context, "请输入使用条件")
                return
            }
            if (0 > coupon.value!!.orderReachPrice!! || 100 < coupon.value!!.orderReachPrice!!) {
                SToast.showToast(v.context, "请使用条件范围为0-100.00")
                return
            }
        } else if (4 == coupon.value?.couponType) {
            if (null == coupon.value?.reduce) {
                SToast.showToast(v.context, "请输入体验价")
                return
            }
            if (0 > coupon.value!!.reduce!! || 100 < coupon.value!!.reduce!!) {
                SToast.showToast(v.context, "请体验价范围为0-100.00")
                return
            }
        }

        if (null == coupon.value?.userCouponCountList?.firstOrNull()?.count) {
            SToast.showToast(v.context, "请输入发券数量")
            return
        }

        if (1 > coupon.value!!.userCouponCountList.firstOrNull()!!.count!! || 10 < coupon.value!!.userCouponCountList.firstOrNull()!!.count!!) {
            SToast.showToast(v.context, "请发券数量范围为1-10")
            return
        }

        if (coupon.value?.endAt.isNullOrEmpty()) {
            SToast.showToast(v.context, "请选择有效期")
            return
        }

        if (2 != coupon.value?.organizationType && coupon.value?.shopIds.isNullOrEmpty()) {
            SToast.showToast(v.context, "请选择门店")
            return
        }

        if (coupon.value?.goodsCategoryIds.isNullOrEmpty()) {
            SToast.showToast(v.context, "请选择设备")
            return
        }

        launch({
            ApiRepository.dealApiResult(
                mDiscountsRepo.submitIssueCoupon(
                    ApiRepository.createRequestBody(
                        GsonUtils.any2Json(coupon.value)
                    )
                )
            )

            withContext(Dispatchers.Main) {
                SToast.showToast(v.context, "发券成功")
            }
        })
    }
}
