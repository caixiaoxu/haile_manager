package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.apiService.DeviceService
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
    private val mRepo = ApiRepository.apiClient(DeviceService::class.java)

    val mDeviceCountStr: MutableLiveData<String> = MutableLiveData()

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
     * 状态数量
     */
    fun requestDeviceStatusTotals() {
        launch(
            {
                val totals = ApiRepository.dealApiResult(
                    mRepo.deviceStatusTotals(hashMapOf())
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
            },
            {
                it.message?.let { it1 -> SToast.showToast(msg = it1) }
                Timber.d("请求失败或异常$it")
            },
            { Timber.d("请求结束") }
        )
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
            val listWrapper = ApiRepository.dealApiResult(
                mRepo.deviceList(
                    hashMapOf(
                        "page" to page,
                        "pageSize" to pageSize,
                        "curWorkStatus" to (curWorkStatus.value ?: "")
                    )
                )
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