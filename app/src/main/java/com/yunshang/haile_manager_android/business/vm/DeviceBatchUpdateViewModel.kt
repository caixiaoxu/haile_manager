package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.business.apiService.CategoryService
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.SkuFuncConfigurationParam
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
class DeviceBatchUpdateViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)
    private val mCategoryRepo = ApiRepository.apiClient(CategoryService::class.java)

    // 所属门店
    val selectDepartments: MutableLiveData<MutableList<SearchSelectParam>> by lazy {
        MutableLiveData()
    }

    val selectDepartmentsVal: LiveData<String> = selectDepartments.map {
        val count: Int = it.size
        if (0 == count) ""
        else if (1 == count) it[0].name
        else "已选中${count}个门店"
    }

    // 设备类型列表
    val categoryList: MutableLiveData<List<CategoryEntity>> by lazy {
        MutableLiveData()
    }
    val selectCategory: MutableLiveData<CategoryEntity> by lazy {
        MutableLiveData()
    }
    val selectCategoryVal: LiveData<String> = selectCategory.map { it?.name ?: "" }

    // 设备型号
    val selectDeviceModel: MutableLiveData<SearchSelectParam?> by lazy {
        MutableLiveData()
    }
    val selectDeviceModelVal: LiveData<String> = selectDeviceModel.map { it?.name ?: "" }

    // 功能配置
    val createDeviceFunConfigure: MutableLiveData<List<SkuFuncConfigurationParam>> by lazy {
        MutableLiveData()
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

    // 是否显示设备配置
    val configShow: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(selectCategory) {
            value = checkConfigShow()
        }
        addSource(selectDeviceModel) {
            value = checkConfigShow()
        }
    }

    /**
     * 检测是否显示配置
     */
    private fun checkConfigShow(): Boolean = (null != selectDeviceModel.value
            && null != selectCategory.value)

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
        addSource(createDeviceFunConfigure) {
            value = checkSubmit()
        }
    }

    /**
     * 检测是否可提交
     */
    private fun checkSubmit(): Boolean = (!selectDepartments.value.isNullOrEmpty()
            && null != selectCategory.value
            && null != selectDeviceModel.value
            && null != createDeviceFunConfigure.value)

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

    fun submit(v: View) {
        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.batchEditDevice(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "shopIdList" to selectDepartments.value?.map { item -> item.id },
                            "categoryId" to selectCategory.value?.id,
                            "spuIdList" to intArrayOf(selectDeviceModel.value!!.id),
                            "functions" to createDeviceFunConfigure.value
                        )
                    )
                )
            )?.let {
                withContext(Dispatchers.Main){
                    SToast.showToast(v.context, "提交成功")
                }
                jump.postValue(0)
            }
        })
    }
}