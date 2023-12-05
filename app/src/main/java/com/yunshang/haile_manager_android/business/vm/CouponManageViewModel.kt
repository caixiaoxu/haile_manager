package com.yunshang.haile_manager_android.business.vm

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CategoryService
import com.yunshang.haile_manager_android.business.apiService.DiscountsService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.CouponEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.rule.DeviceIndicatorEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

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
class CouponManageViewModel : BaseViewModel() {
    private val mDiscountsRepo = ApiRepository.apiClient(DiscountsService::class.java)
    private val mCategoryRepo = ApiRepository.apiClient(CategoryService::class.java)

    val isBatch: MutableLiveData<Boolean> = MutableLiveData(false)

    val couponStatus: List<DeviceIndicatorEntity<Int?>> =
        arrayListOf(
            DeviceIndicatorEntity("全部", MutableLiveData(0), null),
            DeviceIndicatorEntity("未使用", MutableLiveData(0), 1),
            DeviceIndicatorEntity("已使用", MutableLiveData(0), 30),
            DeviceIndicatorEntity("已过期", MutableLiveData(0), 31),
            DeviceIndicatorEntity("已作废", MutableLiveData(0), 32),
        )

    // 券类型
    val couponTypeList =
        StringUtils.getStringArray(R.array.coupon_type).mapIndexed { index, s ->
            SearchSelectParam(index, s)
        }.filter { item -> !item.name.isNullOrEmpty() }.toMutableList().apply {
            add(0, SearchSelectParam(-1, StringUtils.getString(R.string.all_coupon)))
        }

    // 设备类型
    val categoryList: MutableLiveData<MutableList<CategoryEntity>> by lazy {
        MutableLiveData()
    }

    // 当前状态
    var curCouponStatus: MutableLiveData<Int?> = MutableLiveData(couponStatus[0].value)

    // 选择的券类型
    var selectCouponType: MutableLiveData<Int?> = MutableLiveData(null)

    // 选择的门店
    var selectShop: MutableLiveData<Int?> = MutableLiveData(null)

    // 选择的门店
    var selectCategory: MutableLiveData<Int?> = MutableLiveData(null)

    // 券数量
    val mCouponCountStr: MutableLiveData<String> = MutableLiveData()

    val isAll: MutableLiveData<Boolean> = MutableLiveData(false)

    val selectBatchNum: MutableLiveData<Int> = MutableLiveData(0)

    val selectBatchNumVal: LiveData<String> = selectBatchNum.map {
        if (0 == it) "" else "${StringUtils.getString(R.string.selected)} $it"
    }

    /**
     * 请求设备类型U
     */
    fun requestData() {
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
        }, showLoading = false)
    }

    private suspend fun requestCouponNum() {
        ApiRepository.dealApiResult(
            mDiscountsRepo.requestCouponNum(ApiRepository.createRequestBody("{}"))
        )?.let {
            it.couponStatusCountDTOS.forEach { num ->
                when (num.status) {
                    null, 0 -> {
                        couponStatus[0].num.postValue(num.count)
                    }
                    1 -> {
                        couponStatus[1].num.postValue(num.count)
                    }
                    30 -> {
                        couponStatus[2].num.postValue(num.count)
                    }
                    31 -> {
                        couponStatus[3].num.postValue(num.count)
                    }
                    32 -> {
                        couponStatus[4].num.postValue(num.count)
                    }
                }
            }
        }
    }

    /**
     * 刷新券列表
     */
    fun requestCouponList(
        page: Int,
        pageSize: Int,
        result: (listWrapper: ResponseList<CouponEntity>?) -> Unit
    ) {
        launch({
            if (1 == page) {
                requestCouponNum()
            }

            val couponList = ApiRepository.dealApiResult(
                mDiscountsRepo.requestCouponList(
                    ApiRepository.createRequestBody(
                        hashMapOf<String, Any?>(
                            "page" to page,
                            "pageSize" to pageSize,
                            "assetStatus" to curCouponStatus.value,
                            "couponType" to selectCouponType.value,
                        ).also { params ->
                            selectShop.value?.let {
                                params["shopIds"] = listOf(it)
                            }
                            selectCategory.value?.let {
                                params["machineParentTypeIds"] = listOf(it)
                            }
                        }
                    )
                )
            )
            mCouponCountStr.postValue(
                StringUtils.getString(R.string.coupon_num_prompt, couponList?.total ?: 0),
            )
            withContext(Dispatchers.Main) {
                result.invoke(couponList)
            }
        }, {
            Timber.d("请求失败或异常$it")
            withContext(Dispatchers.Main) {
                it.message?.let { it1 -> SToast.showToast(msg = it1) }
                result.invoke(null)
            }
        })
    }


    /**
     * 废弃优惠券
     */
    fun abandonCoupon(context: Context, list: MutableList<CouponEntity>) {
        val idList = list.filter { item -> item.selected }.map { it.id }
        if (idList.isNullOrEmpty()) return
        launch({
            ApiRepository.dealApiResult(
                mDiscountsRepo.abandonCoupon(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "assetIds" to idList
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(context, "批量作废成功")
            }
            LiveDataBus.post(BusEvents.COUPON_LIST_STATUS, true)
        })
    }

    fun refreshSelectBatchNum(list: MutableList<CouponEntity>) {
        selectBatchNum.value = list.count { item -> item.selected }
        isAll.value =
            if (list.isNotEmpty()) list.all { item -> 1 != item.state || item.selected } else false
    }
}