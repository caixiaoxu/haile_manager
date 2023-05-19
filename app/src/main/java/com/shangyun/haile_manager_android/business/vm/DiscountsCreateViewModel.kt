package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.shangyun.haile_manager_android.business.apiService.DiscountsService
import com.shangyun.haile_manager_android.business.event.BusEvents
import com.shangyun.haile_manager_android.data.arguments.ActiveDayParam
import com.shangyun.haile_manager_android.data.arguments.DiscountsParam
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.entities.DiscountsBusinessTypeEntity
import com.shangyun.haile_manager_android.data.entities.DiscountsDetailEntity
import com.shangyun.haile_manager_android.data.entities.DiscountsDeviceTypeEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.utils.DateTimeUtils
import java.util.*

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
class DiscountsCreateViewModel : BaseViewModel() {
    private val mDiscountsRepo = ApiRepository.apiClient(DiscountsService::class.java)

    var id: Int = -1

    // 所属门店
    val createDiscountsShop: MutableLiveData<List<SearchSelectParam>> by lazy {
        MutableLiveData()
    }
    val createDiscountsShopNames: LiveData<String> = createDiscountsShop.map { list ->
        if (list.isNullOrEmpty()) "" else list.joinToString("，") { it.name }
    }

    // 门店业务列表
    val shopBusinessTypeList: MutableLiveData<List<DiscountsBusinessTypeEntity>> by lazy {
        MutableLiveData()
    }
    val selectBusinessType: MutableLiveData<DiscountsBusinessTypeEntity> by lazy {
        MutableLiveData()
    }

    // 设备类型
    val deviceCategoryList: MutableLiveData<List<DiscountsDeviceTypeEntity>> by lazy {
        MutableLiveData()
    }

    // 选择的设备类型
    val selectDeviceCategory: MutableLiveData<List<DiscountsDeviceTypeEntity>> by lazy {
        MutableLiveData()
    }
    val selectDeviceCategoryVal: LiveData<String> = selectDeviceCategory.map { list ->
        if (list.isNullOrEmpty()) "" else list.joinToString("，") { it.name }
    }

    // 开始时间
    val startDateTime: MutableLiveData<Date> by lazy {
        MutableLiveData()
    }
    val startDateTimeVal: LiveData<String> = startDateTime.map {
        DateTimeUtils.formatDateTime(it, "yyyy-MM-dd")
    }

    // 结束时间
    val endDateTime: MutableLiveData<Date> by lazy {
        MutableLiveData()
    }
    val endDateTimeVal: LiveData<String> = endDateTime.map {
        DateTimeUtils.formatDateTime(it, "yyyy-MM-dd")
    }

    val selectActiveModel: MutableLiveData<ActiveDayParam> by lazy {
        MutableLiveData()
    }
    val selectActiveDays: MutableLiveData<List<ActiveDayParam>> by lazy {
        MutableLiveData()
    }
    val selectActiveDayVal: LiveData<String> = selectActiveDays.map { list ->
        if (list.isNullOrEmpty()) "" else list.joinToString(" ") { it.name }
    }

    // 活动时段
    val activeStartTime: MutableLiveData<Date> by lazy {
        MutableLiveData()
    }
    val activeEndTime: MutableLiveData<Date> by lazy {
        MutableLiveData()
    }
    val activeTimeFrame: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    //折扣
    val discounts: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(createDiscountsShop) {
            value = checkSubmit()
        }
        addSource(selectBusinessType) {
            value = checkSubmit()
        }
        addSource(selectDeviceCategory) {
            value = checkSubmit()
        }
        addSource(startDateTime) {
            value = checkSubmit()
        }
        addSource(endDateTime) {
            value = checkSubmit()
        }
        addSource(selectActiveModel) {
            value = checkSubmit()
        }
        addSource(selectActiveDays) {
            value = checkSubmit()
        }
        addSource(activeTimeFrame) {
            value = checkSubmit()
        }
        addSource(discounts) {
            value = checkSubmit()
        }
    }

    /**
     * 检测是否可提交
     */
    private fun checkSubmit(): Boolean =
        !createDiscountsShop.value.isNullOrEmpty() && null != selectBusinessType.value
                && !selectDeviceCategory.value.isNullOrEmpty() && null != startDateTime.value
                && null != endDateTime.value && null != selectActiveModel.value
                && (2 != selectActiveModel.value!!.id || !selectActiveDays.value.isNullOrEmpty())
                && !activeTimeFrame.value.isNullOrEmpty()
                && try {
            if (!discounts.value.isNullOrEmpty()) {
                discounts.value!!.toDouble()
                true
            } else false
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            false
        }

    fun initOldData(detail: DiscountsDetailEntity) {
        id = detail.id
        createDiscountsShop.value = detail.shop
        selectBusinessType.value = DiscountsBusinessTypeEntity(detail.bizType, detail.bizTypeName)
        selectDeviceCategory.value = detail.categoryList
        DateTimeUtils.formatDateFromString(detail.discountStart)?.let {
            startDateTime.value = it
        }
        DateTimeUtils.formatDateFromString(detail.discountEnd)?.let {
            endDateTime.value = it
        }
        DiscountsParam.activeModel.find { it.id == detail.weekDayMode }?.let {
            selectActiveModel.value = it
        }
        selectActiveDays.value =
            detail.weekDayList.mapNotNull { id -> DiscountsParam.activeDay.find { it.id == id } }

        activeTimeFrame.value = detail.timeSpan
        discounts.value = String.format("%.1f", detail.discount * 10)
    }

    /**
     * 初始化数据
     */
    fun requestData(type: Int) {
        launch({
            when (type) {
                1 -> {
                    if (createDiscountsShop.value.isNullOrEmpty()) {
                        return@launch
                    }

                    //设备类型
                    ApiRepository.dealApiResult(
                        mDiscountsRepo.requestDeviceCategory(
                            ApiRepository.createRequestBody(
                                hashMapOf(
                                    "shopIds" to createDiscountsShop.value!!.map { it.id },
                                    "bizTypeId" to selectBusinessType.value!!.id
                                )
                            )
                        )
                    )?.let {
                        deviceCategoryList.postValue(it)
                    }
                }
                else -> {
                    // 业务类型
                    ApiRepository.dealApiResult(
                        mDiscountsRepo.requestBusinessType(
                            ApiRepository.createRequestBody(
                                hashMapOf()
                            )
                        )
                    )?.let {
                        shopBusinessTypeList.postValue(it)
                    }
                }
            }
        })
    }

    /**
     * 创建设备
     */
    fun submit(view: View) {
        launch({
            val params = hashMapOf(
                "shopIdList" to createDiscountsShop.value!!.map { it.id },//店铺
                "bizType" to selectBusinessType.value!!.id,//业务类型
                "categoryIdList" to selectDeviceCategory.value!!.map { it.id },//设备类型
                "startTime" to "${
                    DateTimeUtils.formatDateTime(
                        startDateTime.value!!,
                        "yyyy-MM-dd"
                    )
                } 00:00:00",//开始时间
                "endTime" to "${
                    DateTimeUtils.formatDateTime(
                        endDateTime.value!!,
                        "yyyy-MM-dd"
                    )
                } 23:59:59",//结束时间
                "weekDayMode" to selectActiveModel.value!!.id,//生效日模式。1 全部，2 按天选择，3 工作日，4 周末
                "timeSpan" to activeTimeFrame.value!!,//生效时间
                "discount" to discounts.value!!.toDouble(),//折扣
                "status" to 0,//状态。0 启用，1停用
            )
            if (-1 != id) {
                params["timeId"] = id
            }
            if (2 == selectActiveModel.value!!.id) {
                params["weekDayList"] =
                    selectActiveDays.value!!.map { it.id }//生效日模式。1 全部，2 按天选择，3 工作日，4 周末
            }

            ApiRepository.dealApiResult(
                mDiscountsRepo.saveDiscountsConfig(
                    ApiRepository.createRequestBody(
                        params
                    )
                )
            )
            if (-1 != id) {
                LiveDataBus.post(BusEvents.DISCOUNTS_DETAIL_STATUS, true)
            } else {
                LiveDataBus.post(BusEvents.DISCOUNTS_LIST_STATUS, true)
            }
            jump.postValue(0)
        })
    }

}