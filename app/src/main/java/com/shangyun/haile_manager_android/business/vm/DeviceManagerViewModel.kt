package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.apiService.CategoryService
import com.shangyun.haile_manager_android.business.apiService.DeviceService
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.entities.CategoryEntity
import com.shangyun.haile_manager_android.data.entities.DeviceEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.data.rule.IndicatorEntity
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

    // 状态的工作状态
    val curWorkStatus: MutableLiveData<String> = MutableLiveData("")

    val deviceStatus: MutableLiveData<List<IndicatorEntity<String>>> = MutableLiveData(
        arrayListOf(
            IndicatorEntity("全部", 0, ""),
            IndicatorEntity("运行", 0, "20"),
            IndicatorEntity("空闲", 0, "10"),
            IndicatorEntity("故障", 0, "30"),
            IndicatorEntity("停用", 0, "40"),
        )
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
            },
            {
                it.message?.let { it1 -> SToast.showToast(msg = it1) }
                Timber.d("请求失败或异常$it")
            },
            { Timber.d("请求结束") }
        )
    }

    /**
     * 请求设备类型
     */
    private suspend fun requestDeviceCategory() {
        val list = ApiRepository.dealApiResult(
            mCategoryRepo.category(1)
        )
        list?.let {
            categoryList.postValue(it)
        }
    }

    /**
     * 状态数量
     */
    private suspend fun requestDeviceStatusTotals() {
        val totals = ApiRepository.dealApiResult(
            mDeviceRepo.deviceStatusTotals(hashMapOf())
        )
        deviceStatus.value?.let { list ->
            val titles = arrayListOf<IndicatorEntity<String>>()
            titles.addAll(list)
            titles[1].num = totals?.getTotal(20) ?: 0
            titles[2].num = totals?.getTotal(10) ?: 0
            titles[3].num = totals?.getTotal(30) ?: 0
            titles[4].num = totals?.getTotal(40) ?: 0
            deviceStatus.postValue(titles)
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
            val params: HashMap<String, Any> = hashMapOf(
                "page" to page,
                "pageSize" to pageSize,
                "workStatus" to (curWorkStatus.value ?: ""),
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
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
            withContext(Dispatchers.Main) {
                result.invoke(null)
            }
        }, { Timber.d("请求结束") }, 1 == page)
    }
}