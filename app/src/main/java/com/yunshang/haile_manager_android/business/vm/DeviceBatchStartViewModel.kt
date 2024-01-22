package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.business.apiService.CategoryService
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.ExtAttrDtoItem
import com.yunshang.haile_manager_android.data.entities.ShopAndPositionSelectEntity
import com.yunshang.haile_manager_android.data.entities.SkuUnionIntersectionEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/7/25 09:32
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceBatchStartViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)
    private val mCategoryRepo = ApiRepository.apiClient(CategoryService::class.java)

    // 所属门店
    val selectDepartments: MutableLiveData<MutableList<ShopAndPositionSelectEntity>> by lazy {
        MutableLiveData()
    }

    val selectDepartmentsVal: LiveData<String> = selectDepartments.map {
        when (val count: Int = it.size) {
            0 -> ""
            1 -> it.firstOrNull()?.name ?: ""
            else -> "已选中${count}个营业点"
        }
    }

    // 设备类型列表
    val categoryList: MutableLiveData<List<CategoryEntity>> by lazy {
        MutableLiveData()
    }
    val selectCategory: MutableLiveData<CategoryEntity> by lazy {
        MutableLiveData()
    }
    val selectCategoryVal: LiveData<String> = selectCategory.map { it?.name ?: "" }
    val isDryerOrHair: LiveData<Boolean> = selectCategory.map {
        it?.code?.let { code ->
            DeviceCategory.isDryerOrHair(code)
        } ?: false
    }

    // 是否显示设备型号
    val modelShow: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(selectDepartments) {
            value = checkModelShow()
        }
        addSource(selectCategory) {
            value = checkModelShow()
        }
    }

    /**
     * 检测是否显示型号
     */
    private fun checkModelShow(): Boolean = (!selectDepartments.value.isNullOrEmpty()
            && null != selectCategory.value)

    // 设备型号
    val selectDeviceModel: MutableLiveData<MutableList<SearchSelectParam>?> by lazy {
        MutableLiveData()
    }
    val selectDeviceModelVal: LiveData<String> = selectDeviceModel.map {
        it?.let {
            when (val count: Int = it.size) {
                0 -> ""
                1 -> it[0].name
                else -> "已选中${count}个型号"
            }
        } ?: ""
    }

    // 配置列表
    val funcList: MutableLiveData<MutableList<SkuUnionIntersectionEntity>> by lazy {
        MutableLiveData()
    }
    val selectFunc: MutableLiveData<SkuUnionIntersectionEntity?> by lazy {
        MutableLiveData()
    }
    val selectFuncVal: LiveData<String> = selectFunc.map {
        it?.let { sku ->
            sku.getTitle()
        } ?: ""
    }

    val selectAttr: MutableLiveData<ExtAttrDtoItem?> by lazy {
        MutableLiveData()
    }
    val selectAttrVal: LiveData<String> = selectAttr.map {
        it?.let { it.getTitle() } ?: ""
    }

    fun clearSelectFunc() {
        funcList.value = null
        selectFunc.value = null
        selectAttr.value = null
    }

    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(selectDepartments) {
            value = checkSubmit()
        }
        addSource(selectCategory) {
            value = checkSubmit()
        }
        addSource(selectDeviceModel) {
            value = checkSubmit()
        }
        addSource(selectFunc) {
            value = checkSubmit()
        }
        addSource(selectAttr) {
            value = checkSubmit()
        }
    }

    /**
     * 检测是否可提交
     */
    private fun checkSubmit(): Boolean = (!selectDepartments.value.isNullOrEmpty()
            && null != selectCategory.value
            && null != selectDeviceModel.value
            && null != selectFunc.value
            && (!isDryerOrHair.value!! || null != selectAttr.value))

    /**
     * 请求设备类型
     */
    fun requestDeviceCategory() {
        launch({
            ApiRepository.dealApiResult(
                mCategoryRepo.category(1)
            )?.let {
                categoryList.postValue(it)
            }
        })
    }

    fun requestDeviceSku() {
        if (null == selectDeviceModel.value) return
        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.intersectionSku(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "spuIdList" to selectDeviceModel.value?.map { item ->
                                item.id
                            }
                        )
                    )
                )
            )?.let {
                funcList.postValue(it)
            }
        })
    }

    fun submit(v: View) {
        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.batchStartDevice(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "shopIdList" to selectDepartments.value?.map { item -> item.id },
                            "categoryId" to selectCategory.value?.id,
                            "spuIdList" to selectDeviceModel.value?.map { item ->
                                item.id
                            },
                            "functionId" to selectFunc.value?.id,
                            "time" to selectAttr.value?.unitAmount,
                            "positionIdList" to selectDepartments.value?.flatMap { item ->
                                item.positionList?.mapNotNull { positionItem->positionItem.id } ?: listOf()
                            }
                        )
                    )
                )
            )?.let {
                withContext(Dispatchers.Main) {
                    SToast.showToast(v.context, "启动成功")
                }
                LiveDataBus.post(BusEvents.DEVICE_LIST_STATUS, true)
                jump.postValue(0)
            }
        })
    }
}