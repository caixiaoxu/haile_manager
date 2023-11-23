package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.network.exception.CommonCustomException
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.data.entities.DeviceRepairsEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Title :
 * Author: Lsy
 * Date: 2023/11/23 09:58
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceRepairsReplyListViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)

    var deviceId: Int? = null

    val isBatch: MutableLiveData<Boolean> = MutableLiveData(false)

    val isAll: MutableLiveData<Boolean> = MutableLiveData(false)

    val selectBatchNum: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    fun requestDeviceRepairsList(
        page: Int,
        pageSize: Int,
        callBack: (responseList: ResponseList<out DeviceRepairsEntity>?) -> Unit
    ) {
        launch({
            val result = ApiRepository.dealApiResult(
                mDeviceRepo.requestDeviceRepairsListByDevice(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "page" to page,
                            "pageSize" to pageSize,
                            "deviceId" to deviceId,
                            "replyStatus" to null // 10 未回复，20 已回复
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                callBack(result?.let {res->
                    ResponseList(
                        res.page,
                        res.pageSize,
                        res.total,
                        res.items
                    )
                })
            }
        }, {
            // 自己定义的错误显示报错提示
            Timber.d("请求失败或异常$it")
            withContext(Dispatchers.Main) {
                if (it is CommonCustomException) {
                    it.message?.let { it1 -> SToast.showToast(msg = it1) }
                } else {
                    SToast.showToast(msg = "网络开小差~")
                }
                callBack(null)
            }
        })
    }

    fun refreshSelectBatchNum(list: MutableList<DeviceRepairsEntity>) {
        val selectNum = list.count { item -> item.selected }
        selectBatchNum.value =
            if (0 == selectNum) "" else "${StringUtils.getString(R.string.selected)} $selectNum"
        isAll.value = list.all { item -> 10 != item.replyStatus || item.selected }
    }
}