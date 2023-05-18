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
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.entities.DiscountsBusinessTypeEntity
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

    // 所属门店
    val createDiscountsShop: MutableLiveData<List<SearchSelectParam>> by lazy {
        MutableLiveData()
    }
    val createDiscountsShopNames: LiveData<String> = createDiscountsShop.map { list ->
        list.joinToString("，") { it.name }
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
        list.joinToString("，") { it.name }
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

    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(createDiscountsShop) {
            value = checkSubmit()
        }
    }

    /**
     * 检测是否可提交
     */
    private fun checkSubmit(): Boolean = !createDiscountsShop.value.isNullOrEmpty()


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
            LiveDataBus.post(BusEvents.DEVICE_LIST_STATUS, true)
            jump.postValue(0)
        })
    }

}