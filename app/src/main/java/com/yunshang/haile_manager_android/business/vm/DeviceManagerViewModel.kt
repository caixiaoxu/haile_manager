package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CategoryService
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.DeviceEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.rule.DeviceIndicatorEntity
import com.yunshang.haile_manager_android.data.rule.IndicatorEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

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
class DeviceManagerViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)
    private val mCategoryRepo = ApiRepository.apiClient(CategoryService::class.java)

    // 搜索内容
    val searchKey: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 设备类型
    val categoryList: MutableLiveData<List<CategoryEntity>> = MutableLiveData()

    // 设备数量
    val mDeviceCountStr: MutableLiveData<String> = MutableLiveData()

    // 选择的店铺
    val selectDepartment: MutableLiveData<SearchSelectParam> by lazy {
        MutableLiveData()
    }

    // 选择的设备类型
    val selectDeviceCategory: MutableLiveData<CategoryEntity> by lazy {
        MutableLiveData()
    }

    // 选择的设备类型
    val selectDeviceModel: MutableLiveData<SearchSelectParam> by lazy {
        MutableLiveData()
    }

    // 选择的网络状态
    val selectNetworkStatus: MutableLiveData<SearchSelectParam> by lazy {
        MutableLiveData()
    }

    // 选择的设备状态
    val selectDeviceStatus: MutableLiveData<SearchSelectParam> by lazy {
        MutableLiveData()
    }

    // 状态的工作状态
    val curWorkStatus: MutableLiveData<String> = MutableLiveData("")

    val deviceStatus: List<DeviceIndicatorEntity<String>> =
        arrayListOf(
            DeviceIndicatorEntity("全部", MutableLiveData(0), ""),
            DeviceIndicatorEntity("运行", MutableLiveData(0), "20"),
            DeviceIndicatorEntity("空闲", MutableLiveData(0), "10"),
            DeviceIndicatorEntity("故障", MutableLiveData(0), "30"),
//            IndicatorEntity("停用", 0, "40"),
        )


    /**
     * 请求准备数据
     */
    fun requestData(type: Int) {
        launch(
            {
                when (type) {
                    1 -> requestDeviceCategory()
                    4 -> requestDeviceStatusTotals()
                }
            })
    }

    /**
     * 请求设备类型
     */
    private suspend fun requestDeviceCategory() {
        val list = ApiRepository.dealApiResult(
            mCategoryRepo.category(1)
        )
        list?.let {
            categoryList.postValue(it.filter { e -> DeviceCategory.canShowDeviceCategory(e.code) })
        }
    }

    /**
     * 状态数量
     */
    private suspend fun requestDeviceStatusTotals() {

        val params = hashMapOf<String, Any>()
        // 店铺
        selectDepartment.value?.let {
            params["shopId"] = it.id
        }

        val totals = ApiRepository.dealApiResult(
            mDeviceRepo.deviceStatusTotals(params)
        )
        deviceStatus.let { list ->
            val runningNum = totals?.getTotal(20) ?: 0
            list[1].num.postValue(runningNum)
            val idleNum = totals?.getTotal(10) ?: 0
            list[2].num.postValue(idleNum)
            val falutNum = totals?.getTotal(30) ?: 0
            list[3].num.postValue(falutNum)
            list[0].num.postValue(runningNum + idleNum + falutNum)
//            titles[4].num = totals?.getTotal(40) ?: 0
        }
    }

    /**
     * 刷新设备列表
     */
    fun requestDeviceList(
        page: Int,
        pageSize: Int,
        result: (listWrapper: ResponseList<DeviceEntity>?) -> Unit
    ) {
        launch({
            if (1 == page) {
                requestDeviceStatusTotals()
            }

            val params: HashMap<String, Any> = hashMapOf(
                "page" to page,
                "pageSize" to pageSize,
                "workStatus" to (curWorkStatus.value ?: ""),
                "keywords" to (searchKey.value?.trim() ?: "")
            )
            // 店铺
            selectDepartment.value?.let {
                params["shopId"] = it.id
            }
            // 设备类型
            selectDeviceCategory.value?.let {
                params["categoryId"] = it.id
            }
            // 设备模型
            selectDeviceModel.value?.let {
                params["spuId"] = it.id
            }
            // 网络状态
            selectNetworkStatus.value?.let {
                params["iotStatus"] = it.id
            }
            // 设备状态
            selectDeviceStatus.value?.let {
                params["soldState"] = it.id
            }

            val listWrapper = ApiRepository.dealApiResult(
                mDeviceRepo.deviceList(params)
            )
            listWrapper?.let {
                mDeviceCountStr.postValue(
                    StringUtils.getString(R.string.device_num_hint, it.total),
                )
                withContext(Dispatchers.Main) {
                    result.invoke(it)
                }
            }
        }, {
            Timber.d("请求失败或异常$it")
            withContext(Dispatchers.Main) {
                it.message?.let { it1 -> SToast.showToast(msg = it1) }
                result.invoke(null)
            }
        }, null, 1 == page)
    }
}