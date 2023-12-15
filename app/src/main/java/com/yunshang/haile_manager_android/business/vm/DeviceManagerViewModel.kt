package com.yunshang.haile_manager_android.business.vm

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
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.DeviceEntity
import com.yunshang.haile_manager_android.data.entities.ShopAndPositionSelectEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.rule.DeviceIndicatorEntity
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

    val isBatch: MutableLiveData<Boolean> = MutableLiveData(false)

    val isAll: MutableLiveData<Boolean> = MutableLiveData(false)

    val selectBatchNum: MutableLiveData<Int> = MutableLiveData(0)

    val selectBatchNumVal: LiveData<String> = selectBatchNum.map {
        if (0 == it) "" else "${StringUtils.getString(R.string.selected)} $it"
    }

    // 搜索内容
    val searchKey: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 设备大类型
    var bigCategoryType: Int = -1

    // 设备类型
    val categoryList: MutableLiveData<List<CategoryEntity>> = MutableLiveData()

    // 设备数量
    val mDeviceCountStr: MutableLiveData<String> = MutableLiveData()

    // 选择的店铺
    val selectDepartments: MutableLiveData<MutableList<SearchSelectParam>?> by lazy {
        MutableLiveData()
    }

    // 选择的店铺点位
    val selectDepartmentPositions: MutableLiveData<MutableList<ShopAndPositionSelectEntity>?> by lazy {
        MutableLiveData()
    }

    // 选择的设备类型
    val selectDeviceCategory: MutableLiveData<List<CategoryEntity>?> by lazy {
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
    val curWorkStatus: MutableLiveData<Int?> = MutableLiveData()

    val deviceStatus: List<DeviceIndicatorEntity<Int?>> =
        arrayListOf(
            DeviceIndicatorEntity("全部", MutableLiveData(0), null),
            DeviceIndicatorEntity("运行", MutableLiveData(0), 20),
            DeviceIndicatorEntity("空闲", MutableLiveData(0), 10),
            DeviceIndicatorEntity("故障", MutableLiveData(0), 30),
//            IndicatorEntity("停用", 0, 40),
        )

    val selectErrorStatus: MutableLiveData<Int> by lazy {
        MutableLiveData()
    }
    val errorStatus: List<DeviceIndicatorEntity<Int>> =
        arrayListOf(
            DeviceIndicatorEntity("长时间离线", MutableLiveData(0), 1),
            DeviceIndicatorEntity("7天未使用", MutableLiveData(0), 2),
            DeviceIndicatorEntity("出水故障", MutableLiveData(0), 3),
            DeviceIndicatorEntity("免费设备", MutableLiveData(0), 4),
            DeviceIndicatorEntity("电磁阀异常", MutableLiveData(0), 5),
            DeviceIndicatorEntity("3天未使用", MutableLiveData(0), 6),
        )

    /**
     * 请求准备数据
     */
    fun requestData(type: Int) {
        launch(
            {
                when (type) {
                    1 -> requestDeviceCategory()
                    4 -> {
                        requestDeviceStatusTotals()
                        requestDeviceErrorStatusTotals()
                    }
                }
            }, showLoading = false
        )
    }

    /**
     * 请求设备类型
     */
    private suspend fun requestDeviceCategory() {
        ApiRepository.dealApiResult(
            mCategoryRepo.category(1)
        )?.let {
            categoryList.postValue(it)

            when (bigCategoryType) {
                IntentParams.DeviceManagerParams.CategoryBigType_WashDryer ->
                    selectDeviceCategory.postValue(it.filter { e -> e.code == DeviceCategory.Washing || e.code == DeviceCategory.Dryer || e.code == DeviceCategory.Shoes })
                IntentParams.DeviceManagerParams.CategoryBigType_Hair ->
                    selectDeviceCategory.postValue(it.filter { e -> e.code == DeviceCategory.Hair })
                IntentParams.DeviceManagerParams.CategoryBigType_Shower ->
                    selectDeviceCategory.postValue(it.filter { e -> e.code == DeviceCategory.Shower })
                IntentParams.DeviceManagerParams.CategoryBigType_Dispenser ->
                    selectDeviceCategory.postValue(it.filter { e -> e.code == DeviceCategory.Dispenser })
                IntentParams.DeviceManagerParams.CategoryBigType_Drink ->
                    selectDeviceCategory.postValue(it.filter { e -> e.code == DeviceCategory.Water })
                -1 -> selectDeviceCategory.postValue(null)
            }
        }
    }

    /**
     * 状态数量
     */
    private suspend fun requestDeviceStatusTotals() {
        val params = hashMapOf<String, Any>()
        // 店铺
        selectDepartments.value?.map { it.id }?.joinToString(",")?.let {
            params["shopIdList"] = it
        }
        // 点位
        selectDepartmentPositions.value?.flatMap { item ->
            item.positionList?.mapNotNull { pos -> pos.id } ?: listOf()
        }?.joinToString(",")?.let {
            params["positionIdList"] = it
        }

        // 设备类型
        selectDeviceCategory.value?.let {
            params["categoryIdList"] = it.joinToString(",") { item -> item.id.toString() }
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

    private suspend fun requestDeviceErrorStatusTotals() {
        ApiRepository.dealApiResult(
            mDeviceRepo.deviceErrorStatusTotals(
                ApiRepository.createRequestBody(
                    getDeviceRequestParams(true).also { params ->
                        selectDeviceCategory.value?.map { it.id }?.let {
                            params["categoryIdList"] = it
                        }
                    }
                )
            )
        )?.let { total ->
            errorStatus.let { status ->
                status[0].num.postValue(total.offlineCount)
                status[1].num.postValue(total.unusedCount)
                status[2].num.postValue(total.waterErrorCount)
                status[3].num.postValue(total.freeWaterCount)
                status[4].num.postValue(total.solenoidValveErrorCount)
                status[5].num.postValue(total.unused3Count)
            }
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
                requestDeviceErrorStatusTotals()
            }

            val deviceList = ApiRepository.dealApiResult(
                mDeviceRepo.deviceList(getDeviceRequestParams(false).also { params ->
                    params["page"] = page
                    params["pageSize"] = pageSize
                    // 异常状态
                    selectErrorStatus.value?.let {
                        params["errorStatus"] = it
                    }
                    // 设备类型
                    selectDeviceCategory.value?.joinToString(",") { item -> item.id.toString() }
                        ?.let {
                            params["categoryIdList"] = it
                        }
                })
            )
            mDeviceCountStr.postValue(
                StringUtils.getString(R.string.device_num_hint, deviceList?.total ?: 0),
            )
            withContext(Dispatchers.Main) {
                result.invoke(deviceList)
            }
        }, {
            Timber.d("请求失败或异常$it")
            withContext(Dispatchers.Main) {
                it.message?.let { it1 -> SToast.showToast(msg = it1) }
                result.invoke(null)
            }
        }, null, 1 == page)
    }

    private fun getDeviceRequestParams(isPost: Boolean): HashMap<String, Any> {
        return hashMapOf<String, Any>().also { params ->
            //状态
            curWorkStatus.value?.let {
                params["workStatus"] = it
            }
            // 关键字
            searchKey.value?.trim()?.let {
                params["keywords"] = it
            }
            // 店铺
            val shopIdList = selectDepartments.value?.map { it.id } ?: listOf()
            if (isPost) {
                params["shopIdList"] = shopIdList
            } else {
                params["shopIdList"] = shopIdList.joinToString(",")
            }
            //点位
            val positionIdList = selectDepartmentPositions.value?.flatMap { item ->
                item.positionList?.mapNotNull { pos -> pos.id } ?: listOf()
            } ?: listOf()
            if (isPost) {
                params["positionIdList"] = positionIdList
            } else {
                params["positionIdList"] = positionIdList.joinToString(",")
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
        }
    }

    fun refreshSelectBatchNum(list: MutableList<DeviceEntity>) {
        selectBatchNum.value = list.count { item -> item.selected }
        isAll.value = if (list.isNotEmpty()) list.all { item -> item.selected } else false
    }

    fun preTransferDevice(goodsId: List<Int>, callBack: (type: Int?) -> Unit) {
        if (goodsId.isEmpty()) return
        launch({
            val result = ApiRepository.dealApiResult(
                mDeviceRepo.preTransferDevice(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "goodsIds" to goodsId,
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                callBack(result)
            }
        })
    }

    fun transferDevice(positionId: Int?, goodsId: List<Int>, callBack: () -> Unit) {
        if (null == positionId || goodsId.isEmpty()) return
        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.transferDevice(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "goodsIds" to goodsId,
                            "positionId" to positionId,
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                callBack()
            }
            LiveDataBus.post(BusEvents.SHOP_LIST_STATUS, true)
        })
    }
}